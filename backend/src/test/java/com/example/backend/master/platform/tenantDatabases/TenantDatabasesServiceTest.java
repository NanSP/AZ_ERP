package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantDatabasesServiceTest {

    @Mock
    private TenantDatabasesRepository repository;

    @Mock
    private TenantsRepository tenantsRepository;

    @Mock
    private TemplateMigrationProperties templateMigrationProperties;

    @InjectMocks
    private TenantDatabasesService service;

    void mockTemplateDefaults() {
        lenient().when(templateMigrationProperties.getDatabase()).thenReturn("az_erp_template");
        lenient().when(templateMigrationProperties.getHost()).thenReturn("render-host");
        lenient().when(templateMigrationProperties.getPort()).thenReturn(5432);
        lenient().when(templateMigrationProperties.getUsername()).thenReturn("render-user");
        lenient().when(templateMigrationProperties.getPassword()).thenReturn("render-secret");
    }

    @Test
    void deveCriarTenantDatabaseComDefaults() {
        mockTemplateDefaults();

        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                " az_erp_tenant_1 ",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(tenantsRepository.findById(1L)).thenReturn(Optional.of(criarTenant()));
        when(repository.save(any(TenantDatabases.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TenantDatabases saved = service.criar(request);

        assertEquals("az_erp_tenant_1", saved.getDatabaseName());
        assertEquals("az_erp_template", saved.getTemplateName());
        assertEquals("render-host", saved.getDbHost());
        assertEquals(5432, saved.getDbPort());
        assertEquals("render-user", saved.getDbUsername());
        assertEquals("render-secret", saved.getDbPassword());
        assertEquals("PENDENTE", saved.getProvisionStatus());
    }

    @Test
    void deveBloquearAlteracaoDeHostAposProvisionamento() {
        mockTemplateDefaults();

        TenantDatabases entity = criarDatabase();
        entity.setProvisionStatus("ATIVO");
        entity.setProvisionedAt(LocalDateTime.now());

        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                "az_erp_tenant_1",
                "az_erp_template",
                "novo-host",
                5432,
                "postgres",
                "secret",
                "ATIVO",
                null
        );

        when(repository.findById(2L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(2L, request));

        assertEquals("Nao e permitido alterar DB host apos provisionamento", exception.getMessage());
    }

    @Test
    void deveAtualizarStatusParaAtivoEPreencherProvisionedAt() {
        mockTemplateDefaults();

        TenantDatabases entity = criarDatabase();

        when(repository.findById(2L)).thenReturn(Optional.of(entity));
        when(repository.save(any(TenantDatabases.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TenantDatabases saved = service.atualizarStatusProvisionamento(2L, "ATIVO", LocalDateTime.of(2026, 5, 31, 1, 0));

        assertEquals("ATIVO", saved.getProvisionStatus());
        assertEquals(LocalDateTime.of(2026, 5, 31, 1, 0), saved.getLastCheckAt());
        assertNotNull(saved.getProvisionedAt());
    }

    @Test
    void deveSubstituirPlaceholdersPorConfiguracaoDoServidor() {
        mockTemplateDefaults();

        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                "az_erp_test2",
                "Gerenciado pelo servidor",
                "Gerenciado pelo servidor",
                5432,
                "Gerenciado pelo servidor",
                "********",
                "PENDENTE",
                null
        );

        when(tenantsRepository.findById(1L)).thenReturn(Optional.of(criarTenant()));
        when(repository.save(any(TenantDatabases.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TenantDatabases saved = service.criar(request);

        assertEquals("az_erp_template", saved.getTemplateName());
        assertEquals("render-host", saved.getDbHost());
        assertEquals("render-user", saved.getDbUsername());
        assertEquals("render-secret", saved.getDbPassword());
    }

    @Test
    void deveBloquearCriacaoManualComStatusAtivo() {
        mockTemplateDefaults();

        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                "az_erp_test3",
                null,
                null,
                null,
                null,
                null,
                "ATIVO",
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Provision status ATIVO e reservado ao provisionamento automatico", exception.getMessage());
    }

    private Tenants criarTenant() {
        Tenants tenant = new Tenants();
        tenant.setId(1L);
        tenant.setCodigo("tenant-01");
        tenant.setStatus("PENDENTE");
        return tenant;
    }

    private TenantDatabases criarDatabase() {
        TenantDatabases database = new TenantDatabases();
        database.setId(2L);
        database.setTenantId(criarTenant());
        database.setDatabaseName("az_erp_tenant_1");
        database.setTemplateName("az_erp_template");
        database.setDbHost("localhost");
        database.setDbPort(5432);
        database.setDbUsername("postgres");
        database.setDbPassword("secret");
        database.setProvisionStatus("PENDENTE");
        return database;
    }
}
