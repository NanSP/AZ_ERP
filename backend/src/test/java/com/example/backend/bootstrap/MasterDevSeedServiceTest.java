package com.example.backend.bootstrap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterDevSeedServiceTest {

    @Mock
    private JdbcTemplate masterJdbcTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DevSeedProperties properties;

    @InjectMocks
    private MasterDevSeedService service;

    @Test
    void deveCriarAdminSeedQuandoNaoExistir() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getMasterAdminPassword()).thenReturn("admin123");
        when(passwordEncoder.encode("admin123")).thenReturn("HASH");
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform.system_users WHERE login = ?",
                Integer.class,
                "admin.sistema"
        )).thenReturn(0);

        service.ensureDevAdminSeed();

        verify(masterJdbcTemplate).update(
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
                "HASH",
                "ADMIN_SISTEMA",
                "ATIVO"
        );
    }

    @Test
    void naoDeveCriarAdminSeedQuandoJaExistir() {
        when(properties.isEnabled()).thenReturn(true);
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform.system_users WHERE login = ?",
                Integer.class,
                "admin.sistema"
        )).thenReturn(1);

        service.ensureDevAdminSeed();

        verify(masterJdbcTemplate, never()).update(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.<Object>any()
        );
    }
}
