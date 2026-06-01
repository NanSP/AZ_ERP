package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.templateMigration.TemplateDatabaseAdminService;
import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.master.platform.templateMigration.TemplateRegistry;
import com.example.backend.master.platform.templateMigration.TemplateRegistryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantDatabaseProvisioningServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private TemplateMigrationProperties properties;
    @Mock
    private TemplateRegistryService templateRegistryService;
    @Mock
    private TemplateDatabaseAdminService templateDatabaseAdminService;

    @InjectMocks
    private TenantDatabaseProvisioningService service;

    @Test
    void deveExecutarSqlDeCriacaoDoBanco() {
        TemplateRegistry registry = new TemplateRegistry();
        registry.setDatabaseName("az_erp_template");

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(templateRegistryService.getReadyRegistry()).thenReturn(registry);

        service.createTenantDatabase("tenant_db_01");

        verify(jdbcTemplate).execute("CREATE DATABASE tenant_db_01 TEMPLATE az_erp_template");
        verify(templateRegistryService).acquireLock("CLONING");
        verify(templateRegistryService).markCloneCompleted();
        verify(templateDatabaseAdminService).setConnectionsAllowed("az_erp_template", false);
        verify(templateDatabaseAdminService).setConnectionsAllowed("az_erp_template", true);
        verify(templateDatabaseAdminService).terminateConnections("az_erp_template");
    }

    @Test
    void deveBloquearNomeDeBancoComCaracteresInvalidos() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createTenantDatabase("tenant-db-01")
        );

        assertEquals("Nome do banco contem caracteres invalidos", exception.getMessage());
    }

    @Test
    void deveEncapsularFalhaTecnicaNaCriacaoDoBanco() {
        TemplateRegistry registry = new TemplateRegistry();
        registry.setDatabaseName("az_erp_template");

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(templateRegistryService.getReadyRegistry()).thenReturn(registry);
        doThrow(new RuntimeException("erro postgres"))
                .when(jdbcTemplate)
                .execute("CREATE DATABASE tenant_db_01 TEMPLATE az_erp_template");

        TenantDatabaseProvisioningException exception = assertThrows(
                TenantDatabaseProvisioningException.class,
                () -> service.createTenantDatabase("tenant_db_01")
        );

        assertEquals("Erro ao criar banco do tenant: tenant_db_01", exception.getMessage());
        verify(templateRegistryService).releaseLockToReady();
        verify(templateDatabaseAdminService).setConnectionsAllowed("az_erp_template", true);
    }
}
