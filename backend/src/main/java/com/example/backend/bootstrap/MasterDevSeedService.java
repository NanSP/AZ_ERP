package com.example.backend.bootstrap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MasterDevSeedService {

    private final JdbcTemplate masterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final DevSeedProperties properties;

    public MasterDevSeedService(
            @Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate,
            PasswordEncoder passwordEncoder,
            DevSeedProperties properties
    ) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    public void ensureDevAdminSeed() {
        if (!properties.isEnabled()) {
            return;
        }

        Integer existing = masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform.system_users WHERE login = ?",
                Integer.class,
                "admin.sistema"
        );

        if (existing != null && existing > 0) {
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
                    status
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                "Administrador do Sistema",
                "admin@azerp.com",
                "admin.sistema",
                passwordEncoder.encode(properties.getMasterAdminPassword()),
                "ADMIN_SISTEMA",
                "ATIVO"
        );
    }
}
