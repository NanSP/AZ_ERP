package com.example.backend.tenant.auth;

import com.example.backend.security.JwtService;
import com.example.backend.tenant.context.TenantConnectionInfo;
import com.example.backend.tenant.context.TenantConnectionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;

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

        String sql = """
                SELECT id, login, senha_hash, tipo_usuario, status
                FROM sys.usuarios
                WHERE login = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionInfo.jdbcUrl(),
                        connectionInfo.username(),
                        connectionInfo.password()
                );
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, data.login());

            try (ResultSet rs = statement.executeQuery()) {
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

                String token = jwtService.generateTenantToken(
                        connectionInfo.tenantId(),
                        connectionInfo.tenantCode(),
                        userId,
                        login,
                        role
                );

                return new TenantAuthResponseDTO(
                        token,
                        connectionInfo.tenantId(),
                        connectionInfo.tenantCode(),
                        userId,
                        login,
                        role,
                        "tenant"
                );
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao autenticar usuario do tenant: " + ex.getMessage(), ex);
        }
    }
}
