package com.example.backend.master.platform.templateMigration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateBootstrapServiceTest {

    @Mock
    private TemplateDatabaseAdminService templateDatabaseAdminService;
    @Mock
    private TemplateMigrationProperties properties;
    @Mock
    private TemplateRegistryService templateRegistryService;
    @Mock
    private TemplateMigrationService templateMigrationService;

    @InjectMocks
    private TemplateBootstrapService service;

    @Test
    void deveCriarBancoEMigrarTemplateQuandoAindaNaoExistir() {
        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(templateDatabaseAdminService.databaseExists("az_erp_template")).thenReturn(false);
        when(templateRegistryService.getReadyRegistry()).thenThrow(new RuntimeException("not ready"));

        service.initializeTemplateIfNeeded();

        verify(templateRegistryService).ensureRegistry();
        verify(templateDatabaseAdminService).createDatabase("az_erp_template");
        verify(templateMigrationService).migrateTemplateInterno(null, false);
    }

    @Test
    void naoDeveMigrarTemplateQuandoRegistryJaEstiverPronto() {
        TemplateRegistry registry = new TemplateRegistry();
        registry.setCurrentVersion("V36");
        registry.setStatus("READY");

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(templateDatabaseAdminService.databaseExists("az_erp_template")).thenReturn(true);
        when(templateRegistryService.getReadyRegistry()).thenReturn(registry);

        service.initializeTemplateIfNeeded();

        verify(templateDatabaseAdminService, never()).createDatabase("az_erp_template");
        verify(templateMigrationService, never()).migrateTemplateInterno(null, false);
    }
}
