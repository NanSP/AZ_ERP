package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRequestDTO;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesService;
import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.master.platform.templateMigration.TemplateRegistry;
import com.example.backend.master.platform.templateMigration.TemplateRegistryService;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningRequestDTO;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRequestDTO;
import com.example.backend.master.platform.tenants.TenantsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantProvisioningRegistrationServiceTest {

    @Mock
    private TenantsService tenantsService;
    @Mock
    private TenantDatabasesService tenantDatabasesService;
    @Mock
    private ProvisioningLogsService provisioningLogsService;
    @Mock
    private SystemUsersRepository systemUsersRepository;
    @Mock
    private TemplateRegistryService templateRegistryService;
    @Mock
    private TemplateMigrationProperties templateMigrationProperties;

    @InjectMocks
    private TenantProvisioningRegistrationService service;

    @Test
    void deveRegistrarTenantBancoELogsDoProvisionamento() {
        TenantProvisioningRequestDTO request = new TenantProvisioningRequestDTO(
                5L,
                "TENANT_A",
                "Tenant A",
                "Tenant A LTDA",
                "12345678000199",
                "CNPJ",
                "contato@tenant.com",
                "71999999999",
                "PROFESSIONAL",
                "tenant_a_db",
                "payload-host-ignored",
                6543,
                "payload-user-ignored",
                "payload-pass-ignored",
                "Admin Tenant",
                "admin@tenant.com",
                "admin.tenant",
                "Senha123"
        );

        SystemUsers executor = new SystemUsers();
        executor.setId(5L);

        Tenants tenant = new Tenants();
        tenant.setId(10L);
        tenant.setCodigo("TENANT_A");
        tenant.setNome("Tenant A");

        TenantDatabases tenantDatabase = new TenantDatabases();
        tenantDatabase.setId(20L);
        tenantDatabase.setDatabaseName("tenant_a_db");
        tenantDatabase.setProvisionStatus("PENDENTE");

        TemplateRegistry templateRegistry = new TemplateRegistry();
        templateRegistry.setCurrentVersion("V36");

        when(systemUsersRepository.findById(5L)).thenReturn(Optional.of(executor));
        when(templateRegistryService.getReadyRegistry()).thenReturn(templateRegistry);
        when(templateMigrationProperties.getDatabase()).thenReturn("az_erp_template");
        when(templateMigrationProperties.getHost()).thenReturn("render-internal-host");
        when(templateMigrationProperties.getPort()).thenReturn(5432);
        when(templateMigrationProperties.getUsername()).thenReturn("az_erp_user");
        when(templateMigrationProperties.getPassword()).thenReturn("render-secret");
        when(tenantsService.criar(any(TenantsRequestDTO.class))).thenReturn(tenant);
        when(tenantDatabasesService.criar(any(TenantDatabasesRequestDTO.class))).thenReturn(tenantDatabase);

        RegistrationResult result = service.register(request);

        assertEquals(tenant, result.tenant());
        assertEquals(tenantDatabase, result.tenantDatabase());
        assertEquals(executor, result.executor());
        assertIterableEquals(java.util.List.of("TENANT_CREATED", "DATABASE_REGISTERED"), result.etapasExecutadas());

        ArgumentCaptor<TenantsRequestDTO> tenantRequestCaptor = ArgumentCaptor.forClass(TenantsRequestDTO.class);
        verify(tenantsService).criar(tenantRequestCaptor.capture());
        assertEquals("PENDENTE", tenantRequestCaptor.getValue().status());
        assertEquals("V36", tenantRequestCaptor.getValue().schemaVersion());

        ArgumentCaptor<TenantDatabasesRequestDTO> databaseRequestCaptor = ArgumentCaptor.forClass(TenantDatabasesRequestDTO.class);
        verify(tenantDatabasesService).criar(databaseRequestCaptor.capture());
        assertEquals("az_erp_template", databaseRequestCaptor.getValue().templateName());
        assertEquals("render-internal-host", databaseRequestCaptor.getValue().dbHost());
        assertEquals(5432, databaseRequestCaptor.getValue().dbPort());
        assertEquals("az_erp_user", databaseRequestCaptor.getValue().dbUsername());
        assertEquals("render-secret", databaseRequestCaptor.getValue().dbPassword());
        assertEquals("PENDENTE", databaseRequestCaptor.getValue().provisionStatus());

        ArgumentCaptor<ProvisioningLogsRequestDTO> logCaptor = ArgumentCaptor.forClass(ProvisioningLogsRequestDTO.class);
        verify(provisioningLogsService, times(2)).criar(logCaptor.capture());
        assertEquals("TENANT_CREATED", logCaptor.getAllValues().get(0).etapa());
        assertEquals("DATABASE_REGISTERED", logCaptor.getAllValues().get(1).etapa());
    }
}
