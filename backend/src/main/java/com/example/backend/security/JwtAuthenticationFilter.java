package com.example.backend.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.security.SecurityUserPrincipal;
import com.example.backend.tenant.auth.TenantAuthService;
import com.example.backend.tenant.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TenantAuthService tenantAuthService;

    public JwtAuthenticationFilter(JwtService jwtService, TenantAuthService tenantAuthService) {
        this.jwtService = jwtService;
        this.tenantAuthService = tenantAuthService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractToken(request.getHeader("Authorization"), request.getCookies());

        try {
            if (token != null && !token.isBlank()) {
                DecodedJWT decodedJWT = jwtService.validateToken(token);

                String scope = decodedJWT.getClaim("scope").asString();
                Long tenantId = null;
                String tenantCode = null;

                SecurityUserPrincipal principal;

                if ("tenant".equalsIgnoreCase(scope)) {
                    tenantId = decodedJWT.getClaim("tenantId").asLong();
                    tenantCode = decodedJWT.getClaim("tenantCode").asString();
                    TenantContext.setTenant(tenantCode);
                    principal = tenantAuthService.loadPrincipal(
                            tenantId,
                            tenantCode,
                            decodedJWT.getClaim("userId").asLong()
                    );
                } else {
                    List<String> perfis = readStringList(decodedJWT.getClaim("perfis"));
                    List<String> permissoes = readStringList(decodedJWT.getClaim("permissoes"));

                    principal = new SecurityUserPrincipal(
                            decodedJWT.getClaim("userId").asLong(),
                            decodedJWT.getSubject(),
                            decodedJWT.getClaim("role").asString(),
                            scope,
                            tenantId,
                            tenantCode,
                            perfis,
                            permissoes
                    );
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            TenantContext.clear();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token invalido ou expirado\"}");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private List<String> readStringList(Claim claim) {
        List<String> values = claim.asList(String.class);
        return values != null ? values : Collections.emptyList();
    }

    private String extractToken(String authHeader, Cookie[] cookies) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (AuthCookieService.AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
