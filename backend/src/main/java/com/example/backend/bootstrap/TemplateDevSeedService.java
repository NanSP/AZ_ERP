package com.example.backend.bootstrap;

import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.shared.exception.ValidacaoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class TemplateDevSeedService {

    private final TemplateMigrationProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final DevSeedProperties devSeedProperties;

    public TemplateDevSeedService(
            TemplateMigrationProperties properties,
            PasswordEncoder passwordEncoder,
            DevSeedProperties devSeedProperties
    ) {
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
        this.devSeedProperties = devSeedProperties;
    }

    public void ensureTemplateDevUsers() {
        if (!devSeedProperties.isEnabled()) {
            return;
        }

        try (
                Connection connection = DriverManager.getConnection(
                        properties.buildJdbcUrl(),
                        properties.getUsername(),
                        properties.getPassword()
                )
        ) {
            ensureUserWithProfile(
                    connection,
                    "Master Tecnico",
                    "master@tenant.local",
                    "master.tenant",
                    devSeedProperties.getTenantTechnicalPassword(),
                    "sistema",
                    "ativo",
                    "MASTER_TECNICO"
            );
            ensureUserWithProfile(
                    connection,
                    "Administrador Tenant",
                    "admin@tenant.local",
                    "admin.tenant",
                    devSeedProperties.getTenantAdminPassword(),
                    "administrador",
                    "ativo",
                    "ADMIN_TENANT"
            );
            ensureUserWithProfile(
                    connection,
                    "Usuario Padrao",
                    "user@tenant.local",
                    "user.tenant",
                    devSeedProperties.getTenantUserPassword(),
                    "usuario",
                    "ativo",
                    "USUARIO_PADRAO"
            );
        } catch (SQLException ex) {
            throw new ValidacaoException("Erro ao garantir usuarios de desenvolvimento do template: " + ex.getMessage());
        }
    }

    private void ensureUserWithProfile(
            Connection connection,
            String nome,
            String email,
            String login,
            String senha,
            String tipoUsuario,
            String status,
            String perfil
    ) throws SQLException {
        Integer usuarioId = findUserId(connection, login);
        if (usuarioId == null) {
            usuarioId = insertUser(connection, nome, email, login, senha, tipoUsuario, status);
        }

        Integer perfilId = findProfileId(connection, perfil);
        if (perfilId == null) {
            throw new ValidacaoException("Perfil de desenvolvimento nao encontrado no template: " + perfil);
        }

        if (!userProfileExists(connection, usuarioId, perfilId)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO sys.usuario_perfil (usuario_id, perfil_id) VALUES (?, ?)"
            )) {
                statement.setInt(1, usuarioId);
                statement.setInt(2, perfilId);
                statement.executeUpdate();
            }
        }
    }

    private Integer findUserId(Connection connection, String login) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM sys.usuarios WHERE login = ?"
        )) {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }

    private Integer insertUser(
            Connection connection,
            String nome,
            String email,
            String login,
            String senha,
            String tipoUsuario,
            String status
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
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
        )) {
            statement.setString(1, nome);
            statement.setString(2, email);
            statement.setString(3, login);
            statement.setString(4, passwordEncoder.encode(senha));
            statement.setString(5, tipoUsuario);
            statement.setString(6, status);

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    private Integer findProfileId(Connection connection, String profileName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM sys.perfis WHERE nome = ?"
        )) {
            statement.setString(1, profileName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }

    private boolean userProfileExists(Connection connection, Integer userId, Integer profileId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM sys.usuario_perfil WHERE usuario_id = ? AND perfil_id = ?"
        )) {
            statement.setInt(1, userId);
            statement.setInt(2, profileId);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
