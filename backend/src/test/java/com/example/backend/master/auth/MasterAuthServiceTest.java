package com.example.backend.master.auth;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.security.JwtService;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterAuthServiceTest {

    @Mock
    private SystemUsersRepository systemUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private MasterAuthService service;

    @Test
    void deveAutenticarUsuarioAtivoEAtualizarUltimoAcesso() {
        AuthRequestDTO request = new AuthRequestDTO(" Admin.Login ", "segredo123");
        SystemUsers user = criarUsuario("ATIVO");

        when(systemUsersRepository.findByLogin("admin.login")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("segredo123", "senha-hash")).thenReturn(true);
        when(jwtService.generateToken(10L, "admin.login", "MASTER_ADMIN", "master")).thenReturn("jwt-token");

        AuthResponseDTO response = service.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(10L, response.userId());
        assertEquals("admin.login", response.login());
        assertEquals("MASTER_ADMIN", response.role());
        assertEquals("master", response.contexto());

        ArgumentCaptor<SystemUsers> userCaptor = ArgumentCaptor.forClass(SystemUsers.class);
        verify(systemUsersRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getUltimoAcesso());
    }

    @Test
    void deveFalharQuandoUsuarioEstiverSuspenso() {
        AuthRequestDTO request = new AuthRequestDTO("admin.login", "segredo123");
        SystemUsers user = criarUsuario("SUSPENSO");

        when(systemUsersRepository.findByLogin("admin.login")).thenReturn(Optional.of(user));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.login(request));

        assertEquals("Usuario suspenso", exception.getMessage());
    }

    @Test
    void deveFalharQuandoSenhaForInvalida() {
        AuthRequestDTO request = new AuthRequestDTO("admin.login", "senha-errada");
        SystemUsers user = criarUsuario("ATIVO");

        when(systemUsersRepository.findByLogin("admin.login")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-errada", "senha-hash")).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.login(request));

        assertEquals("Login ou senha invalidos", exception.getMessage());
    }

    private SystemUsers criarUsuario(String status) {
        SystemUsers user = new SystemUsers();
        user.setId(10L);
        user.setLogin("admin.login");
        user.setSenha("senha-hash");
        user.setRole("MASTER_ADMIN");
        user.setStatus(status);
        user.setUltimoAcesso((LocalDateTime) null);
        return user;
    }
}
