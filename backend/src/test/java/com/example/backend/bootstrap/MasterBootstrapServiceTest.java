package com.example.backend.bootstrap;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterBootstrapServiceTest {

    @Mock
    private JdbcTemplate masterJdbcTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MasterBootstrapProperties properties;

    @InjectMocks
    private MasterBootstrapService service;

    @Test
    void deveCriarPrimeiroAdminQuandoBootstrapEstiverHabilitado() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getNome()).thenReturn("Administrador Master");
        when(properties.getEmail()).thenReturn("admin@azerp.com");
        when(properties.getLogin()).thenReturn("admin.master");
        when(properties.getPassword()).thenReturn("SenhaSegura123");
        when(properties.isForcePasswordChange()).thenReturn(true);
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform.system_users WHERE role = ?",
                Integer.class,
                "ADMIN_SISTEMA"
        )).thenReturn(0);
        when(passwordEncoder.encode("SenhaSegura123")).thenReturn("HASH");

        service.ensureInitialAdmin();

        verify(masterJdbcTemplate).update(
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
                "Administrador Master",
                "admin@azerp.com",
                "admin.master",
                "HASH",
                "ADMIN_SISTEMA",
                "ATIVO",
                true,
                null
        );
    }

    @Test
    void deveFalharQuandoSenhaDoBootstrapForFraca() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getNome()).thenReturn("Administrador Master");
        when(properties.getEmail()).thenReturn("admin@azerp.com");
        when(properties.getLogin()).thenReturn("admin.master");
        when(properties.getPassword()).thenReturn("Senha123");

        ValidacaoException exception = assertThrows(ValidacaoException.class, service::ensureInitialAdmin);

        assertEquals("Senha do bootstrap master deve ter pelo menos 12 caracteres", exception.getMessage());
        verify(masterJdbcTemplate, never()).update(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.<Object>any()
        );
    }
}
