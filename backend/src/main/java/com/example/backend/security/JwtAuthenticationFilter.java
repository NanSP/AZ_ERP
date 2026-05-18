package com.example.backend.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.tenant.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

        try {
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

                List<String> perfis = readStringList(decodedJWT.getClaim("perfis"));
                List<String> permissoes = readStringList(decodedJWT.getClaim("permissoes"));

                SecurityUserPrincipal principal = new SecurityUserPrincipal(
                        decodedJWT.getClaim("userId").asLong(),
                        decodedJWT.getSubject(),
                        decodedJWT.getClaim("role").asString(),
                        scope,
                        tenantId,
                        tenantCode,
                        perfis,
                        permissoes
                );

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
}