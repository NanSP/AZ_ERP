package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.shared.db.PostgresJdbcUrlBuilder;
import com.example.backend.shared.exception.ValidacaoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class TenantApplicationUserProvisioningService {

    private final PasswordEncoder passwordEncoder;

    public TenantApplicationUserProvisioningService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void createInitialAdminUser(
            TenantDatabases tenantDatabase,
            String nome,
            String email,
            String login,
            String senha
    ) {
        String jdbcUrl = PostgresJdbcUrlBuilder.build(
                tenantDatabase.getDbHost(),
                tenantDatabase.getDbPort(),
                tenantDatabase.getDatabaseName()
        );

        try (
                Connection connection = DriverManager.getConnection(
                        jdbcUrl,
                        tenantDatabase.getDbUsername(),
                        tenantDatabase.getDbPassword()
                )
        ) {
            if (userExists(connection, login, email)) {
                throw new ValidacaoException("Usuario inicial do tenant ja existe no banco provisionado");
            }

            Integer userId = insertUser(connection, nome, email, login, senha);
            Integer profileId = findAdminTenantProfileId(connection);

            if (profileId == null) {
                throw new ValidacaoException("Perfil ADMIN_TENANT nao encontrado no banco do tenant");
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO sys.usuario_perfil (usuario_id, perfil_id) VALUES (?, ?)"
            )) {
                statement.setInt(1, userId);
                statement.setInt(2, profileId);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ValidacaoException("Erro ao criar usuario inicial no banco do tenant: " + ex.getMessage());
        }
    }

    private boolean userExists(Connection connection, String login, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM sys.usuarios WHERE login = ? OR email = ?"
        )) {
            statement.setString(1, login);
            statement.setString(2, email);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private Integer insertUser(
            Connection connection,
            String nome,
            String email,
            String login,
            String senha
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
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
        )) {
            statement.setString(1, nome);
            statement.setString(2, email);
            statement.setString(3, login);
            statement.setString(4, passwordEncoder.encode(senha));
            statement.setString(5, "administrador");
            statement.setString(6, "ativo");

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    private Integer findAdminTenantProfileId(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM sys.perfis WHERE nome = 'ADMIN_TENANT'"
        )) {
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }
}
