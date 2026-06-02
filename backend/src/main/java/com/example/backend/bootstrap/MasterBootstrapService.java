package com.example.backend.bootstrap;

import com.example.backend.shared.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MasterBootstrapService {

    private final JdbcTemplate masterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final MasterBootstrapProperties properties;

    public MasterBootstrapService(
            @Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate,
            PasswordEncoder passwordEncoder,
            MasterBootstrapProperties properties
    ) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    public void ensureInitialAdmin() {
        if (!properties.isEnabled()) {
            return;
        }

        validarConfiguracao();

        Integer existingAdmins = masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform.system_users WHERE role = ?",
                Integer.class,
                "ADMIN_SISTEMA"
        );

        if (existingAdmins != null && existingAdmins > 0) {
            return;
        }

        masterJdbcTemplate.update(
                """
                INSERT INTO platform.system_users (
                    nome,
                    email,
                    login,
                    senha_hash,
                    role,
                    status,
                    password_change_required,
                    password_changed_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                properties.getNome().trim(),
                properties.getEmail().trim().toLowerCase(),
                properties.getLogin().trim().toLowerCase(),
                passwordEncoder.encode(properties.getPassword()),
                "ADMIN_SISTEMA",
                "ATIVO",
                properties.isForcePasswordChange(),
                null
        );
    }

    private void validarConfiguracao() {
        if (isBlank(properties.getNome())) {
            throw new ValidacaoException("Nome do bootstrap master e obrigatorio");
        }

        if (isBlank(properties.getEmail())) {
            throw new ValidacaoException("Email do bootstrap master e obrigatorio");
        }

        if (isBlank(properties.getLogin())) {
            throw new ValidacaoException("Login do bootstrap master e obrigatorio");
        }

        if (isBlank(properties.getPassword())) {
            throw new ValidacaoException("Senha do bootstrap master e obrigatoria");
        }

        if (properties.getPassword().trim().length() < 12) {
            throw new ValidacaoException("Senha do bootstrap master deve ter pelo menos 12 caracteres");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
