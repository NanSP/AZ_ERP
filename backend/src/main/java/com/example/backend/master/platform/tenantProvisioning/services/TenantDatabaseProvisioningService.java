package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.templateMigration.TemplateDatabaseAdminService;
import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.master.platform.templateMigration.TemplateRegistry;
import com.example.backend.master.platform.templateMigration.TemplateRegistryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TenantDatabaseProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final TemplateMigrationProperties properties;
    private final TemplateRegistryService templateRegistryService;
    private final TemplateDatabaseAdminService templateDatabaseAdminService;

    public TenantDatabaseProvisioningService(
            @Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate,
            TemplateMigrationProperties properties,
            TemplateRegistryService templateRegistryService,
            TemplateDatabaseAdminService templateDatabaseAdminService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.templateRegistryService = templateRegistryService;
        this.templateDatabaseAdminService = templateDatabaseAdminService;
    }

    public void createTenantDatabase(String databaseName) {
        validarNomeBanco(databaseName);
        TemplateRegistry registry = templateRegistryService.getReadyRegistry();
        templateRegistryService.acquireLock("CLONING");
        String sql = String.format(
                "CREATE DATABASE %s TEMPLATE %s",
                templateDatabaseAdminService.quoteIdentifier(databaseName),
                templateDatabaseAdminService.quoteIdentifier(registry.getDatabaseName())
        );

        boolean connectionsDisabled = false;

        try {
            templateDatabaseAdminService.setConnectionsAllowed(properties.getDatabase(), false);
            connectionsDisabled = true;
            templateDatabaseAdminService.terminateConnections(properties.getDatabase());
            jdbcTemplate.execute(sql);
            templateRegistryService.markCloneCompleted();
        } catch (Exception ex) {
            templateRegistryService.releaseLockToReady();
            throw new TenantDatabaseProvisioningException(
                    "Erro ao criar banco do tenant: " + databaseName,
                    ex
            );
        } finally {
            if (connectionsDisabled) {
                templateDatabaseAdminService.setConnectionsAllowed(properties.getDatabase(), true);
            }
        }
    }

    private void validarNomeBanco(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("Nome do banco e obrigatorio");
        }

        if (!databaseName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Nome do banco contem caracteres invalidos");
        }
    }
}
