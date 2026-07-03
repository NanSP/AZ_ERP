package com.example.backend.tenant.auth;

import com.example.backend.auth.ChangePasswordRequestDTO;
import com.example.backend.auth.AuthSessionResponseDTO;
import com.example.backend.auth.PasswordChangeResponseDTO;
import com.example.backend.security.AuthCookieService;
import com.example.backend.security.SecurityUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/auth")
public class TenantAuthController {

    private final TenantAuthService service;
    private final AuthCookieService authCookieService;

    public TenantAuthController(TenantAuthService service, AuthCookieService authCookieService) {
        this.service = service;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/login")
    public TenantAuthResponseDTO login(
            @RequestBody TenantAuthRequestDTO data,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TenantAuthResponseDTO auth = service.login(data);
        authCookieService.attachAuthCookie(response, auth.token(), request);
        return auth;
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public TenantForgotPasswordResponseDTO forgotPassword(@RequestBody TenantForgotPasswordRequestDTO data) {
        return service.forgotPassword(data);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public AuthSessionResponseDTO me(Authentication authentication) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) authentication.getPrincipal();
        return service.me(principal);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public PasswordChangeResponseDTO changePassword(
            @RequestBody ChangePasswordRequestDTO data,
            Authentication authentication
    ) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) authentication.getPrincipal();
        return service.changePassword(principal, data);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authCookieService.clearAuthCookie(response, request);
    }
}
