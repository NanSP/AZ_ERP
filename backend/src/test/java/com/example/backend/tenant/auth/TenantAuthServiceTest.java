package com.example.backend.tenant.auth;

import com.example.backend.security.JwtService;
import com.example.backend.tenant.context.TenantConnectionInfo;
import com.example.backend.tenant.context.TenantConnectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantAuthServiceTest {

    @Mock
    private TenantConnectionService tenantConnectionService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement userStatement;
    @Mock
    private PreparedStatement perfisStatement;
    @Mock
    private PreparedStatement permissoesStatement;
    @Mock
    private ResultSet userResultSet;
    @Mock
    private ResultSet perfisResultSet;
    @Mock
    private ResultSet permissoesResultSet;

    @Test
    void deveAutenticarUsuarioDoTenantComPerfisEPermissoes() throws Exception {
        TenantAuthService service = new TenantAuthService(tenantConnectionService, passwordEncoder, jwtService);
        TenantAuthRequestDTO request = new TenantAuthRequestDTO("TENANT_A", "joao", "Senha123");
        TenantConnectionInfo connectionInfo = new TenantConnectionInfo(
                1L,
                "TENANT_A",
                "tenant_a_db",
                "localhost",
                5432,
                "tenant_user",
                "tenant_pass"
        );

        when(tenantConnectionService.resolve("TENANT_A")).thenReturn(connectionInfo);
        when(connection.prepareStatement(anyString())).thenReturn(userStatement, perfisStatement, permissoesStatement);
        when(userStatement.executeQuery()).thenReturn(userResultSet);
        when(perfisStatement.executeQuery()).thenReturn(perfisResultSet);
        when(permissoesStatement.executeQuery()).thenReturn(permissoesResultSet);

        when(userResultSet.next()).thenReturn(true, false);
        when(userResultSet.getLong("id")).thenReturn(10L);
        when(userResultSet.getString("login")).thenReturn("joao");
        when(userResultSet.getString("senha_hash")).thenReturn("HASH");
        when(userResultSet.getString("tipo_usuario")).thenReturn("ADMIN");
        when(userResultSet.getString("status")).thenReturn("ativo");

        when(passwordEncoder.matches("Senha123", "HASH")).thenReturn(true);

        when(perfisResultSet.next()).thenReturn(true, true, false);
        when(perfisResultSet.getString("nome")).thenReturn("ADMIN", "GESTOR");

        when(permissoesResultSet.next()).thenReturn(true, true, false);
        when(permissoesResultSet.getString("modulo")).thenReturn("sys", "fi");
        when(permissoesResultSet.getString("recurso")).thenReturn("usuarios", "contas_pagar");
        when(permissoesResultSet.getString("acao")).thenReturn("read", "update");

        when(jwtService.generateTenantToken(
                1L,
                "TENANT_A",
                10L,
                "joao",
                "ADMIN",
                List.of("ADMIN", "GESTOR"),
                List.of("sys:usuarios:read", "fi:contas_pagar:update")
        )).thenReturn("TOKEN_TENANT");

        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tenant_a_db",
                    "tenant_user",
                    "tenant_pass"
            )).thenReturn(connection);

            TenantAuthResponseDTO response = service.login(request);

            assertEquals("TOKEN_TENANT", response.token());
            assertEquals(1L, response.tenantId());
            assertEquals("TENANT_A", response.tenantCode());
            assertEquals(10L, response.userId());
            assertEquals("joao", response.login());
            assertEquals("ADMIN", response.role());
            assertEquals("tenant", response.scope());
            assertEquals(List.of("ADMIN", "GESTOR"), response.perfis());
            assertEquals(List.of("sys:usuarios:read", "fi:contas_pagar:update"), response.permissoes());
        }

        verify(userStatement).setString(1, "joao");
        verify(perfisStatement).setLong(1, 10L);
        verify(permissoesStatement).setLong(1, 10L);
    }

    @Test
    void deveBloquearUsuarioInativoDoTenant() throws Exception {
        TenantAuthService service = new TenantAuthService(tenantConnectionService, passwordEncoder, jwtService);
        TenantAuthRequestDTO request = new TenantAuthRequestDTO("TENANT_A", "joao", "Senha123");

        when(tenantConnectionService.resolve("TENANT_A")).thenReturn(new TenantConnectionInfo(
                1L, "TENANT_A", "tenant_a_db", "localhost", 5432, "tenant_user", "tenant_pass"
        ));
        when(connection.prepareStatement(anyString())).thenReturn(userStatement, perfisStatement, permissoesStatement);
        when(userStatement.executeQuery()).thenReturn(userResultSet);
        when(userResultSet.next()).thenReturn(true);
        when(userResultSet.getLong("id")).thenReturn(10L);
        when(userResultSet.getString("login")).thenReturn("joao");
        when(userResultSet.getString("senha_hash")).thenReturn("HASH");
        when(userResultSet.getString("tipo_usuario")).thenReturn("ADMIN");
        when(userResultSet.getString("status")).thenReturn("inativo");

        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tenant_a_db",
                    "tenant_user",
                    "tenant_pass"
            )).thenReturn(connection);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> service.login(request));

            assertEquals("Usuario inativo", exception.getMessage());
        }

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void deveBloquearSenhaInvalidaDoTenant() throws Exception {
        TenantAuthService service = new TenantAuthService(tenantConnectionService, passwordEncoder, jwtService);
        TenantAuthRequestDTO request = new TenantAuthRequestDTO("TENANT_A", "joao", "Senha123");

        when(tenantConnectionService.resolve("TENANT_A")).thenReturn(new TenantConnectionInfo(
                1L, "TENANT_A", "tenant_a_db", "localhost", 5432, "tenant_user", "tenant_pass"
        ));
        when(connection.prepareStatement(anyString())).thenReturn(userStatement, perfisStatement, permissoesStatement);
        when(userStatement.executeQuery()).thenReturn(userResultSet);
        when(userResultSet.next()).thenReturn(true);
        when(userResultSet.getLong("id")).thenReturn(10L);
        when(userResultSet.getString("login")).thenReturn("joao");
        when(userResultSet.getString("senha_hash")).thenReturn("HASH");
        when(userResultSet.getString("tipo_usuario")).thenReturn("ADMIN");
        when(userResultSet.getString("status")).thenReturn("ativo");
        when(passwordEncoder.matches("Senha123", "HASH")).thenReturn(false);

        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tenant_a_db",
                    "tenant_user",
                    "tenant_pass"
            )).thenReturn(connection);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> service.login(request));

            assertEquals("Login ou senha invalidos", exception.getMessage());
        }

        verify(jwtService, never()).generateTenantToken(
                anyLong(),
                anyString(),
                anyLong(),
                anyString(),
                anyString(),
                eq(List.of()),
                eq(List.of())
        );
    }
}
