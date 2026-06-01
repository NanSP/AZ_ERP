package com.example.backend.master.platform.templateMigration;

import org.springframework.stereotype.Service;

@Service
public class TemplateBootstrapService {

    private final TemplateDatabaseAdminService templateDatabaseAdminService;
    private final TemplateMigrationProperties properties;
    private final TemplateRegistryService templateRegistryService;
    private final TemplateMigrationService templateMigrationService;

    public TemplateBootstrapService(
            TemplateDatabaseAdminService templateDatabaseAdminService,
            TemplateMigrationProperties properties,
            TemplateRegistryService templateRegistryService,
            TemplateMigrationService templateMigrationService
    ) {
        this.templateDatabaseAdminService = templateDatabaseAdminService;
        this.properties = properties;
        this.templateRegistryService = templateRegistryService;
        this.templateMigrationService = templateMigrationService;
    }

    public void initializeTemplateIfNeeded() {
        templateRegistryService.ensureRegistry();

        if (!templateDatabaseAdminService.databaseExists(properties.getDatabase())) {
            templateDatabaseAdminService.createDatabase(properties.getDatabase());
        }

        if (!templateRegistryPronto()) {
            templateMigrationService.migrateTemplateInterno(null, false);
        }
    }

    private boolean templateRegistryPronto() {
        try {
            templateRegistryService.getReadyRegistry();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
