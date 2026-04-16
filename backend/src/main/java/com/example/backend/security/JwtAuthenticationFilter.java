package com.example.backend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT decodedJWT = jwtService.validateToken(token);

            String scope = decodedJWT.getClaim("scope").asString();
            Long tenantId = null;
            String tenantCode = null;

            if ("tenant".equalsIgnoreCase(scope)) {
                tenantId = decodedJWT.getClaim("tenantId").asLong();
                tenantCode = decodedJWT.getClaim("tenantCode").asString();
            }

            SecurityUserPrincipal principal = new SecurityUserPrincipal(
                    decodedJWT.getClaim("userId").asLong(),
                    decodedJWT.getSubject(),
                    decodedJWT.getClaim("role").asString(),
                    scope,
                    tenantId,
                    tenantCode
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
