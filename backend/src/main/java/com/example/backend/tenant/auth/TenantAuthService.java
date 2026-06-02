package com.example.backend.tenant.auth;

import com.example.backend.auth.ChangePasswordRequestDTO;
import com.example.backend.auth.PasswordChangeResponseDTO;
import com.example.backend.security.JwtService;
import com.example.backend.security.SecurityUserPrincipal;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.tenant.context.TenantConnectionInfo;
import com.example.backend.tenant.context.TenantConnectionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;

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
                SELECT id, login, senha_hash, tipo_usuario, status, expiracao_senha
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
                    throw new ValidacaoException("Login ou senha invalidos");
                }

                Long userId = rs.getLong("id");
                String login = rs.getString("login");
                String senhaHash = rs.getString("senha_hash");
                String role = rs.getString("tipo_usuario");
                String status = rs.getString("status");
                Date expiracaoSenha = rs.getDate("expiracao_senha");

                if (!"ativo".equalsIgnoreCase(status)) {
                    throw new ValidacaoException("Usuario inativo");
                }

                if (!passwordEncoder.matches(data.senha(), senhaHash)) {
                    throw new ValidacaoException("Login ou senha invalidos");
                }

                List<String> perfis = carregarPerfis(perfisStatement, userId);
                List<String> permissoes = carregarPermissoes(permissoesStatement, userId);
                boolean passwordChangeRequired = expiracaoSenha != null
                        && !expiracaoSenha.toLocalDate().isAfter(LocalDate.now());

                atualizarUltimoAcesso(connection, userId);

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
                        passwordChangeRequired,
                        perfis,
                        permissoes
                );
            }

        } catch (SQLException ex) {
            throw new ValidacaoException("Erro ao autenticar usuario do tenant: " + ex.getMessage());
        }
    }

    public PasswordChangeResponseDTO changePassword(SecurityUserPrincipal principal, ChangePasswordRequestDTO data) {
        validarTrocaSenha(data);

        TenantConnectionInfo connectionInfo = tenantConnectionService.resolve(principal.getTenantCode());

        String selectSql = """
                SELECT senha_hash
                FROM sys.usuarios
                WHERE id = ?
                """;
        String updateSql = """
                UPDATE sys.usuarios
                SET senha_hash = ?, expiracao_senha = NULL, tentativas_login = 0
                WHERE id = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        connectionInfo.jdbcUrl(),
                        connectionInfo.username(),
                        connectionInfo.password()
                );
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                PreparedStatement updateStatement = connection.prepareStatement(updateSql)
        ) {
            selectStatement.setLong(1, principal.getUserId());

            try (ResultSet rs = selectStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new ValidacaoException("Usuario autenticado do tenant nao encontrado");
                }

                String senhaHash = rs.getString("senha_hash");
                if (!passwordEncoder.matches(data.senhaAtual(), senhaHash)) {
                    throw new ValidacaoException("Senha atual invalida");
                }

                if (passwordEncoder.matches(data.novaSenha(), senhaHash)) {
                    throw new ValidacaoException("Nova senha deve ser diferente da senha atual");
                }
            }

            updateStatement.setString(1, passwordEncoder.encode(data.novaSenha()));
            updateStatement.setLong(2, principal.getUserId());
            updateStatement.executeUpdate();

            return new PasswordChangeResponseDTO("Senha alterada com sucesso");
        } catch (SQLException ex) {
            throw new ValidacaoException("Erro ao alterar senha do tenant: " + ex.getMessage());
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

    private void atualizarUltimoAcesso(Connection connection, Long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE sys.usuarios SET ultimo_acesso = CURRENT_TIMESTAMP WHERE id = ?"
        )) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    private void validarTrocaSenha(ChangePasswordRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados de troca de senha sao obrigatorios");
        }

        if (data.senhaAtual() == null || data.senhaAtual().isBlank()) {
            throw new ValidacaoException("Senha atual e obrigatoria");
        }

        if (data.novaSenha() == null || data.novaSenha().isBlank()) {
            throw new ValidacaoException("Nova senha e obrigatoria");
        }

        if (data.novaSenha().trim().length() < 8) {
            throw new ValidacaoException("Nova senha deve ter pelo menos 8 caracteres");
        }
    }
}
