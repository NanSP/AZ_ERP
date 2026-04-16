package com.example.backend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.tenant.context.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
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

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                DecodedJWT decodedJWT = jwtService.validateToken(token);

                String scope = decodedJWT.getClaim("scope").asString();
                Long tenantId = null;
                String tenantCode = null;

                if ("tenant".equalsIgnoreCase(scope)) {
                    tenantId = decodedJWT.getClaim("tenantId").asLong();
                    tenantCode = decodedJWT.getClaim("tenantCode").asString();
                    TenantContext.setTenant(tenantCode);
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
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            TenantContext.clear();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token invalido ou expirado\"}");
        } finally {
            TenantContext.clear();
        }
    }
}
