package com.example.backend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    @Test
    void deveGerarEValidarTokenMaster() {
        JwtService service = new JwtService(criarProperties());

        String token = service.generateToken(10L, "admin", "MASTER_ADMIN", "master");
        DecodedJWT decoded = service.validateToken(token);

        assertEquals("az-erp", decoded.getIssuer());
        assertEquals("admin", service.extractLogin(token));
        assertEquals(10L, service.extractUserId(token));
        assertEquals("MASTER_ADMIN", service.extractRole(token));
        assertEquals("master", service.extractScope(token));
        assertNotNull(decoded.getExpiresAtAsInstant());
    }

    @Test
    void deveGerarTokenTenantComClaimsCompletas() {
        JwtService service = new JwtService(criarProperties());

        String token = service.generateTenantToken(
                2L,
                "TENANT_A",
                20L,
                "joao",
                "OPERADOR",
                List.of("ADMIN", "GESTOR"),
                List.of("sys:usuarios:read", "fi:contas_pagar:update")
        );
        DecodedJWT decoded = service.validateToken(token);

        assertEquals("tenant", service.extractScope(token));
        assertEquals(2L, service.extractTenantId(token));
        assertEquals("TENANT_A", service.extractTenantCode(token));
        assertEquals(20L, service.extractUserId(token));
        assertEquals(List.of("ADMIN", "GESTOR"), decoded.getClaim("perfis").asList(String.class));
    }

    private JwtProperties criarProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("segredo-super-seguro");
        properties.setIssuer("az-erp");
        properties.setExpirationHours(2L);
        return properties;
    }
}
