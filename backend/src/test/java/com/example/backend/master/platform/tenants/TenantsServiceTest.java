package com.example.backend.master.platform.tenants;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsersRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantsServiceTest {

    @Mock
    private TenantsRepository repository;
    @Mock
    private TenantDatabasesRepository tenantDatabasesRepository;
    @Mock
    private TenantAdminUsersRepository tenantAdminUsersRepository;
    @Mock
    private ProvisioningLogsRepository provisioningLogsRepository;

    @InjectMocks
    private TenantsService service;

    @Test
    void deveCriarTenantComNormalizacaoEDefaults() {
        TenantsRequestDTO request = new TenantsRequestDTO(
                " tenant-01 ",
                " Tenant 01 ",
                " Fantasia ",
                "12.345.678/0001-99",
                "cnpj",
                "OWNER@TENANT.COM",
                "71999999999",
                null,
                null,
                null,
                " observacao "
        );

        when(repository.save(any(Tenants.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenants saved = service.criar(request);

        assertEquals("tenant-01", saved.getCodigo());
        assertEquals("Tenant 01", saved.getNome());
        assertEquals("12345678000199", saved.getDocumento());
        assertEquals("CNPJ", saved.getTipoDocumento());
        assertEquals("owner@tenant.com", saved.getEmailResponsavel());
        assertEquals("PENDENTE", saved.getStatus());
        assertEquals("STARTER", saved.getPlano());
        assertEquals("V1", saved.getSchemaVersion());
    }

    @Test
    void deveBloquearAlteracaoDePlanoAposProvisionamento() {
        Tenants entity = criarTenant();
        entity.setPlano("STARTER");

        TenantsRequestDTO request = new TenantsRequestDTO(
                "tenant-01",
                "Tenant 01",
                null,
                "12345678000199",
                "CNPJ",
                "owner@tenant.com",
                null,
                "PENDENTE",
                "ENTERPRISE",
                "V1",
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(tenantDatabasesRepository.existsByTenantIdId(1L)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(1L, request));

        assertEquals("Nao e permitido alterar o plano do tenant apos o provisionamento", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoTenantEstiverEmUso() {
        Tenants entity = criarTenant();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(provisioningLogsRepository.existsByTenantIdId(1L)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1L));

        assertEquals("Nao e permitido excluir tenant com infraestrutura ou historico associado", exception.getMessage());
        verify(repository, never()).delete(any(Tenants.class));
    }

    @Test
    void deveAtualizarSchemaVersionPorFluxoInterno() {
        Tenants entity = criarTenant();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(Tenants.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenants updated = service.atualizarSchemaVersionInterna(1L, "V36");

        assertEquals("V36", updated.getSchemaVersion());
    }

    private Tenants criarTenant() {
        Tenants tenant = new Tenants();
        tenant.setId(1L);
        tenant.setCodigo("tenant-01");
        tenant.setNome("Tenant 01");
        tenant.setDocumento("12345678000199");
        tenant.setTipoDocumento("CNPJ");
        tenant.setEmailResponsavel("owner@tenant.com");
        tenant.setStatus("PENDENTE");
        tenant.setPlano("STARTER");
        tenant.setSchemaVersion("V1");
        return tenant;
    }
}
