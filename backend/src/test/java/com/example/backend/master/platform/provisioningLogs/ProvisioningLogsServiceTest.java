package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvisioningLogsServiceTest {

    @Mock
    private ProvisioningLogsRepository repository;

    @Mock
    private TenantsRepository tenantsRepository;

    @Mock
    private SystemUsersRepository systemUsersRepository;

    @InjectMocks
    private ProvisioningLogsService service;

    @Test
    void deveCriarLogSemTenantParaOperacoesDeTemplate() {
        ProvisioningLogsRequestDTO request = new ProvisioningLogsRequestDTO(
                null,
                "TEMPLATE_INFO",
                "INFO",
                "Consulta executada",
                Map.of("database", "az_erp_template"),
                99L
        );

        when(systemUsersRepository.findById(99L)).thenReturn(Optional.of(criarExecutor()));
        when(repository.save(any(ProvisioningLogs.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProvisioningLogs saved = service.criar(request);

        assertNull(saved.getTenantId());
        assertEquals("TEMPLATE_INFO", saved.getEtapa());
        assertEquals("INFO", saved.getStatus());

        ArgumentCaptor<ProvisioningLogs> captor = ArgumentCaptor.forClass(ProvisioningLogs.class);
        verify(repository).save(captor.capture());
        assertEquals("Consulta executada", captor.getValue().getMensagem());
    }

    @Test
    void deveBloquearLogQuandoTenantEstiverForaDoCicloOperacional() {
        ProvisioningLogsRequestDTO request = new ProvisioningLogsRequestDTO(
                1L,
                "DATABASE_CREATED",
                "ERRO",
                "Falha no banco",
                Map.of("erro", "timeout"),
                99L
        );
        Tenants tenant = new Tenants();
        tenant.setId(1L);
        tenant.setStatus("INATIVO");

        when(tenantsRepository.findById(1L)).thenReturn(Optional.of(tenant));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido registrar provisioning log para tenant fora do ciclo operacional", exception.getMessage());
    }

    private SystemUsers criarExecutor() {
        SystemUsers executor = new SystemUsers();
        executor.setId(99L);
        executor.setLogin("ops.user");
        executor.setStatus("ATIVO");
        return executor;
    }
}
