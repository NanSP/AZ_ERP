package com.example.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthCookieService {
    public static final String AUTH_COOKIE_NAME = "az_erp_auth";

    public void attachAuthCookie(HttpServletResponse response, String token, HttpServletRequest request) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie(token, request).toString());
    }

    public void clearAuthCookie(HttpServletResponse response, HttpServletRequest request) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildClearedAuthCookie(request).toString());
    }

    private ResponseCookie buildAuthCookie(String token, HttpServletRequest request) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(isSecureRequest(request))
                .sameSite("Lax")
                .path("/")
                .maxAge(60L * 60L * 24L)
                .build();
    }

    private ResponseCookie buildClearedAuthCookie(HttpServletRequest request) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(isSecureRequest(request))
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    private boolean isSecureRequest(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
    }
}
