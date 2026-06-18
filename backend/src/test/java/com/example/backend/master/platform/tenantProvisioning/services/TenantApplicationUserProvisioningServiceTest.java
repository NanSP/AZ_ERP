package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.shared.exception.ValidacaoException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantApplicationUserProvisioningServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement userExistsStatement;
    @Mock
    private PreparedStatement insertUserStatement;
    @Mock
    private PreparedStatement profileStatement;
    @Mock
    private PreparedStatement userProfileStatement;
    @Mock
    private ResultSet userExistsResultSet;
    @Mock
    private ResultSet insertUserResultSet;
    @Mock
    private ResultSet profileResultSet;

    @Test
    void deveCriarUsuarioInicialNoBancoDoTenant() throws Exception {
        TenantApplicationUserProvisioningService service = new TenantApplicationUserProvisioningService(passwordEncoder);
        TenantDatabases tenantDatabase = criarTenantDatabase();

        when(passwordEncoder.encode("Senha12345")).thenReturn("HASH");
        when(connection.prepareStatement("SELECT COUNT(*) FROM sys.usuarios WHERE login = ? OR email = ?"))
                .thenReturn(userExistsStatement);
        when(connection.prepareStatement(
                """
                INSERT INTO sys.usuarios (
                    nome,
                    email,
                    login,
                    senha_hash,
                    tipo_usuario,
                    status,
                    expiracao_senha
                ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
                RETURNING id
                """
        )).thenReturn(insertUserStatement);
        when(connection.prepareStatement("SELECT id FROM sys.perfis WHERE nome = 'ADMIN_TENANT'"))
                .thenReturn(profileStatement);
        when(connection.prepareStatement("INSERT INTO sys.usuario_perfil (usuario_id, perfil_id) VALUES (?, ?)"))
                .thenReturn(userProfileStatement);

        when(userExistsStatement.executeQuery()).thenReturn(userExistsResultSet);
        when(userExistsResultSet.next()).thenReturn(true);
        when(userExistsResultSet.getInt(1)).thenReturn(0);

        when(insertUserStatement.executeQuery()).thenReturn(insertUserResultSet);
        when(insertUserResultSet.next()).thenReturn(true);
        when(insertUserResultSet.getInt("id")).thenReturn(15);

        when(profileStatement.executeQuery()).thenReturn(profileResultSet);
        when(profileResultSet.next()).thenReturn(true);
        when(profileResultSet.getInt("id")).thenReturn(9);

        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tenant_db?sslmode=require",
                    "tenant_user",
                    "tenant_pass"
            )).thenReturn(connection);

            service.createInitialAdminUser(
                    tenantDatabase,
                    "Admin Tenant",
                    "admin@tenant.com",
                    "admin.tenant",
                    "Senha12345"
            );
        }

        verify(userProfileStatement).setInt(1, 15);
        verify(userProfileStatement).setInt(2, 9);
        verify(userProfileStatement).executeUpdate();
    }

    @Test
    void deveBloquearQuandoUsuarioJaExistirNoBancoDoTenant() throws Exception {
        TenantApplicationUserProvisioningService service = new TenantApplicationUserProvisioningService(passwordEncoder);
        TenantDatabases tenantDatabase = criarTenantDatabase();

        when(connection.prepareStatement("SELECT COUNT(*) FROM sys.usuarios WHERE login = ? OR email = ?"))
                .thenReturn(userExistsStatement);
        when(userExistsStatement.executeQuery()).thenReturn(userExistsResultSet);
        when(userExistsResultSet.next()).thenReturn(true);
        when(userExistsResultSet.getInt(1)).thenReturn(1);

        ValidacaoException exception;
        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/tenant_db?sslmode=require",
                    "tenant_user",
                    "tenant_pass"
            )).thenReturn(connection);

            exception = assertThrows(
                    ValidacaoException.class,
                    () -> service.createInitialAdminUser(
                            tenantDatabase,
                            "Admin Tenant",
                            "admin@tenant.com",
                            "admin.tenant",
                            "Senha12345"
                    )
            );
        }

        assertEquals("Usuario inicial do tenant ja existe no banco provisionado", exception.getMessage());
    }

    private TenantDatabases criarTenantDatabase() {
        TenantDatabases tenantDatabase = new TenantDatabases();
        tenantDatabase.setDatabaseName("tenant_db");
        tenantDatabase.setDbHost("localhost");
        tenantDatabase.setDbPort(5432);
        tenantDatabase.setDbUsername("tenant_user");
        tenantDatabase.setDbPassword("tenant_pass");
        return tenantDatabase;
    }
}
