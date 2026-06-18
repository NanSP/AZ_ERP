package com.example.backend.master.platform.systemUsers;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemUsersServiceTest {

    @Mock
    private SystemUsersRepository repository;

    @Mock
    private ProvisioningLogsRepository provisioningLogsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SystemUsersService service;

    @Test
    void deveCriarSystemUserComSenhaHash() {
        SystemUsersRequestDTO request = new SystemUsersRequestDTO(
                "Master Admin",
                "MASTER@ERP.COM",
                " Admin.Root ",
                "senhaForte123",
                "MASTER_ADMIN",
                "ATIVO"
        );

        when(passwordEncoder.encode("senhaForte123")).thenReturn("senha-hash");
        when(repository.save(any(SystemUsers.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemUsers saved = service.criar(request);

        assertEquals("master@erp.com", saved.getEmail());
        assertEquals("admin.root", saved.getLogin());
        assertEquals("senha-hash", saved.getSenha());

        ArgumentCaptor<SystemUsers> captor = ArgumentCaptor.forClass(SystemUsers.class);
        verify(repository).save(captor.capture());
        assertEquals("ADMIN_SISTEMA", captor.getValue().getRole());
    }

    @Test
    void deveBloquearAlteracaoDeEmailAposUsoOperacional() {
        SystemUsers entity = criarSystemUser();
        entity.setUltimoAcesso(LocalDateTime.now());

        SystemUsersRequestDTO request = new SystemUsersRequestDTO(
                "Master Admin",
                "novo@erp.com",
                "admin.root",
                null,
                "MASTER_ADMIN",
                "ATIVO"
        );

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10L, request));

        assertEquals("Nao e permitido alterar o email apos uso operacional", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoPossuirLogsVinculados() {
        SystemUsers entity = criarSystemUser();

        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(provisioningLogsRepository.existsByExecutadoPorId(10L)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10L));

        assertEquals("System user nao pode ser excluido pois possui logs vinculados", exception.getMessage());
        verify(repository, never()).delete(any(SystemUsers.class));
    }

    private SystemUsers criarSystemUser() {
        SystemUsers user = new SystemUsers();
        user.setId(10L);
        user.setNome("Master Admin");
        user.setEmail("master@erp.com");
        user.setLogin("admin.root");
        user.setSenha("senha-hash");
        user.setRole("ADMIN_SISTEMA");
        user.setStatus("ATIVO");
        return user;
    }
}
