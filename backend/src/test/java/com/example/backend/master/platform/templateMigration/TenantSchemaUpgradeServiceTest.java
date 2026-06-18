package com.example.backend.master.platform.templateMigration;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.master.platform.tenants.TenantsService;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantSchemaUpgradeServiceTest {

    @Mock
    private TenantsRepository tenantsRepository;
    @Mock
    private TenantDatabasesRepository tenantDatabasesRepository;
    @Mock
    private TenantsService tenantsService;
    @Mock
    private ProvisioningLogsService provisioningLogsService;

    @InjectMocks
    private TenantSchemaUpgradeService service;

    @Test
    void deveExecutarUpgradeIncrementalSomenteParaTenantDesatualizado() {
        Tenants outdated = criarTenant(1L, "ATIVO", "V35");
        Tenants updated = criarTenant(2L, "ATIVO", "V36");
        TenantDatabases database = criarDatabase(outdated);

        FluentConfiguration configuration = mock(FluentConfiguration.class);
        Flyway flyway = mock(Flyway.class);

        when(tenantsRepository.findAll()).thenReturn(List.of(outdated, updated));
        when(tenantDatabasesRepository.findByTenantId(outdated)).thenReturn(Optional.of(database));
        when(configuration.dataSource("jdbc:postgresql://localhost:5432/tenant_1_db?sslmode=require", "tenant_user", "tenant_pass"))
                .thenReturn(configuration);
        when(configuration.locations("classpath:db/migration/template")).thenReturn(configuration);
        when(configuration.baselineOnMigrate(true)).thenReturn(configuration);
        when(configuration.load()).thenReturn(flyway);

        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            flywayStatic.when(Flyway::configure).thenReturn(configuration);
            service.upgradeOutdatedTenants(99L, "V36");
        }

        verify(flyway).migrate();
        verify(tenantsService).atualizarSchemaVersionInterna(1L, "V36");
        verify(tenantsService, never()).atualizarSchemaVersionInterna(2L, "V36");

        ArgumentCaptor<ProvisioningLogsRequestDTO> captor = ArgumentCaptor.forClass(ProvisioningLogsRequestDTO.class);
        verify(provisioningLogsService, times(2)).criar(captor.capture());
        assertEquals("TENANT_SCHEMA_UPGRADE_STARTED", captor.getAllValues().get(0).etapa());
        assertEquals("TENANT_SCHEMA_UPGRADE_FINISHED", captor.getAllValues().get(1).etapa());
    }

    private Tenants criarTenant(Long id, String status, String schemaVersion) {
        Tenants tenant = new Tenants();
        tenant.setId(id);
        tenant.setStatus(status);
        tenant.setSchemaVersion(schemaVersion);
        return tenant;
    }

    private TenantDatabases criarDatabase(Tenants tenant) {
        TenantDatabases database = new TenantDatabases();
        database.setTenantId(tenant);
        database.setDatabaseName("tenant_1_db");
        database.setDbHost("localhost");
        database.setDbPort(5432);
        database.setDbUsername("tenant_user");
        database.setDbPassword("tenant_pass");
        database.setProvisionStatus("ATIVO");
        return database;
    }
}
