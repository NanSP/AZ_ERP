package com.example.backend.tenant.auth;

import com.example.backend.security.JwtService;
import com.example.backend.tenant.context.TenantConnectionInfo;
import com.example.backend.tenant.context.TenantConnectionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TenantAuthService {

    private final TenantConnectionService tenantConnectionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TenantAuthService(
            TenantConnectionService tenantConnectionService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.tenantConnectionService = tenantConnectionService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TenantAuthResponseDTO login(TenantAuthRequestDTO data) {
        TenantConnectionInfo connectionInfo = tenantConnectionService.resolve(data.tenantCode());

        String userSql = """
                SELECT id, login, senha_hash, tipo_usuario, status
                FROM sys.usuarios
                WHERE login = ?
                """;
        String perfisSql = """
                SELECT p.nome
                FROM sys.usuario_perfil up
                JOIN sys.perfis p ON p.id = up.perfil_id
                WHERE up.usuario_id = ?
                ORDER BY p.nome
                """;
        String permissoesSql = """
                SELECT DISTINCT
                    pe.modulo,
                    pe.recurso,
                    pe.acao
                FROM sys.usuario_perfil up
                JOIN sys.perfil_permissao pp ON pp.perfil_id = up.perfil_id
                JOIN sys.permissoes pe ON pe.id = pp.permissao_id
                WHERE up.usuario_id = ?
                ORDER BY pe.modulo, pe.recurso, pe.acao
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionInfo.jdbcUrl(),
                        connectionInfo.username(),
                        connectionInfo.password()
                );
                PreparedStatement userStatement = connection.prepareStatement(userSql);
                PreparedStatement perfisStatement = connection.prepareStatement(perfisSql);
                PreparedStatement permissoesStatement = connection.prepareStatement(permissoesSql)
        ) {
            userStatement.setString(1, data.login());

            try (ResultSet rs = userStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Login ou senha invalidos");
                }

                Long userId = rs.getLong("id");
                String login = rs.getString("login");
                String senhaHash = rs.getString("senha_hash");
                String role = rs.getString("tipo_usuario");
                String status = rs.getString("status");

                if (!"ativo".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Usuario inativo");
                }

                if (!passwordEncoder.matches(data.senha(), senhaHash)) {
                    throw new RuntimeException("Login ou senha invalidos");
                }

                List<String> perfis = carregarPerfis(perfisStatement, userId);
                List<String> permissoes = carregarPermissoes(permissoesStatement, userId);

                String token = jwtService.generateTenantToken(
                        connectionInfo.tenantId(),
                        connectionInfo.tenantCode(),
                        userId,
                        login,
                        role,
                        perfis,
                        permissoes
                );

                return new TenantAuthResponseDTO(
                        token,
                        connectionInfo.tenantId(),
                        connectionInfo.tenantCode(),
                        userId,
                        login,
                        role,
                        "tenant",
                        perfis,
                        permissoes
                );
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao autenticar usuario do tenant: " + ex.getMessage(), ex);
        }
    }

    private List<String> carregarPerfis(PreparedStatement statement, Long userId) throws SQLException {
        statement.setLong(1, userId);

        List<String> perfis = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                perfis.add(rs.getString("nome"));
            }
        }

        return perfis;
    }

    private List<String> carregarPermissoes(PreparedStatement statement, Long userId) throws SQLException {
        statement.setLong(1, userId);

        Set<String> permissoes = new LinkedHashSet<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                permissoes.add(
                        rs.getString("modulo")
                                + ":"
                                + rs.getString("recurso")
                                + ":"
                                + rs.getString("acao")
                );
            }
        }

        return new ArrayList<>(permissoes);
    }
}
