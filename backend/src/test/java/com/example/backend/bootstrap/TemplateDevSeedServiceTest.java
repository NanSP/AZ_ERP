package com.example.backend.bootstrap;

import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
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

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateDevSeedServiceTest {

    @Mock
    private TemplateMigrationProperties properties;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DevSeedProperties devSeedProperties;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement findUserStatement;
    @Mock
    private PreparedStatement insertUserStatement;
    @Mock
    private PreparedStatement findProfileStatement;
    @Mock
    private PreparedStatement userProfileExistsStatement;
    @Mock
    private PreparedStatement insertUserProfileStatement;
    @Mock
    private ResultSet emptyUserResultSet;
    @Mock
    private ResultSet insertUserResultSet;
    @Mock
    private ResultSet profileResultSet;
    @Mock
    private ResultSet userProfileExistsResultSet;

    @Test
    void deveCriarUsuariosDevDoTemplateQuandoAusentes() throws Exception {
        TemplateDevSeedService service = new TemplateDevSeedService(properties, passwordEncoder, devSeedProperties);

        when(devSeedProperties.isEnabled()).thenReturn(true);
        when(devSeedProperties.getTenantTechnicalPassword()).thenReturn("admin123");
        when(devSeedProperties.getTenantAdminPassword()).thenReturn("admin123");
        when(devSeedProperties.getTenantUserPassword()).thenReturn("user123");
        when(properties.buildJdbcUrl()).thenReturn("jdbc:postgresql://localhost:5432/az_erp_template");
        when(properties.getUsername()).thenReturn("postgres");
        when(properties.getPassword()).thenReturn("secret");

        when(passwordEncoder.encode("admin123")).thenReturn("HASH_ADMIN");
        when(passwordEncoder.encode("user123")).thenReturn("HASH_USER");

        when(connection.prepareStatement("SELECT id FROM sys.usuarios WHERE login = ?"))
                .thenReturn(findUserStatement);
        when(connection.prepareStatement(
                """
                INSERT INTO sys.usuarios (
                    nome,
                    email,
                    login,
                    senha_hash,
                    tipo_usuario,
                    status
                ) VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """
        )).thenReturn(insertUserStatement);
        when(connection.prepareStatement("SELECT id FROM sys.perfis WHERE nome = ?"))
                .thenReturn(findProfileStatement);
        when(connection.prepareStatement("SELECT COUNT(*) FROM sys.usuario_perfil WHERE usuario_id = ? AND perfil_id = ?"))
                .thenReturn(userProfileExistsStatement);
        when(connection.prepareStatement("INSERT INTO sys.usuario_perfil (usuario_id, perfil_id) VALUES (?, ?)"))
                .thenReturn(insertUserProfileStatement);

        when(findUserStatement.executeQuery()).thenReturn(emptyUserResultSet);
        when(emptyUserResultSet.next()).thenReturn(false);

        when(insertUserStatement.executeQuery()).thenReturn(insertUserResultSet);
        when(insertUserResultSet.next()).thenReturn(true);
        when(insertUserResultSet.getInt("id")).thenReturn(11, 12, 13);

        when(findProfileStatement.executeQuery()).thenReturn(profileResultSet);
        when(profileResultSet.next()).thenReturn(true, true, true);
        when(profileResultSet.getInt("id")).thenReturn(101, 102, 103);

        when(userProfileExistsStatement.executeQuery()).thenReturn(userProfileExistsResultSet);
        when(userProfileExistsResultSet.next()).thenReturn(true, true, true);
        when(userProfileExistsResultSet.getInt(1)).thenReturn(0, 0, 0);

        try (MockedStatic<DriverManager> driverManager = mockStatic(DriverManager.class)) {
            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/az_erp_template",
                    "postgres",
                    "secret"
            )).thenReturn(connection);

            service.ensureTemplateDevUsers();
        }

        verify(insertUserStatement).setString(3, "master.tenant");
        verify(insertUserStatement).setString(3, "admin.tenant");
        verify(insertUserStatement).setString(3, "user.tenant");
        verify(insertUserProfileStatement, times(3)).executeUpdate();
    }
}
