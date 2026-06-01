package com.example.backend.master.platform.templateMigration;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.shared.exception.ValidacaoException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateMigrationService {

    private final TemplateMigrationProperties properties;
    private final ProvisioningLogsService provisioningLogsService;
    private final TemplateRegistryService templateRegistryService;
    private final TemplateDatabaseAdminService templateDatabaseAdminService;
    private final TenantSchemaUpgradeService tenantSchemaUpgradeService;

    public TemplateMigrationService(
            TemplateMigrationProperties properties,
            ProvisioningLogsService provisioningLogsService,
            TemplateRegistryService templateRegistryService,
            TemplateDatabaseAdminService templateDatabaseAdminService,
            TenantSchemaUpgradeService tenantSchemaUpgradeService
    ) {
        this.properties = properties;
        this.provisioningLogsService = provisioningLogsService;
        this.templateRegistryService = templateRegistryService;
        this.templateDatabaseAdminService = templateDatabaseAdminService;
        this.tenantSchemaUpgradeService = tenantSchemaUpgradeService;
    }

    public void migrateTemplate(Long systemUserId) {
        migrateTemplateInterno(systemUserId, true);
    }

    public void validateTemplate(Long systemUserId) {
        try {
            templateRegistryService.acquireLock("VALIDATING");
            templateDatabaseAdminService.terminateConnections(properties.getDatabase());

            Flyway flyway = Flyway.configure()
                    .dataSource(
                            properties.buildJdbcUrl(),
                            properties.getUsername(),
                            properties.getPassword()
                    )
                    .locations("classpath:db/migration/template")
                    .baselineOnMigrate(true)
                    .load();

            flyway.validate();

            salvarLog(
                    systemUserId,
                    "TEMPLATE_VALIDATION",
                    "SUCESSO",
                    "Validacao do banco template executada com sucesso",
                    Map.of(
                    "database", properties.getDatabase(),
                    "location", "classpath:db/migration/template"
            )
            );

            templateRegistryService.markValidated();

        } catch (Exception ex) {
            templateRegistryService.markError("ERROR");
            salvarLog(
                    systemUserId,
                    "TEMPLATE_VALIDATION",
                    "ERRO",
                    "Erro na validacao do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "location", "classpath:db/migration/template",
                            "erro", ex.getMessage()
                    )
            );

            throw new ValidacaoException("Erro ao validar template: " + ex.getMessage());
        }
    }

    public String infoTemplate(Long systemUserId) {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(
                            properties.buildJdbcUrl(),
                            properties.getUsername(),
                            properties.getPassword()
                    )
                    .locations("classpath:db/migration/template")
                    .baselineOnMigrate(true)
                    .load();

            MigrationInfo current = flyway.info().current();
            String info = current == null
                    ? "Template sem versao aplicada"
                    : "Versao atual do template: " + current.getVersion() + " - " + current.getDescription();

            salvarLog(
                    systemUserId,
                    "TEMPLATE_INFO",
                    "INFO",
                    "Consulta de versao do banco template executada",
                    Map.of(
                            "database", properties.getDatabase(),
                            "info", info
                    )
            );

            return info;

        } catch (Exception ex) {
            salvarLog(
                    systemUserId,
                    "TEMPLATE_INFO",
                    "ERRO",
                    "Erro ao consultar informacoes do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "erro", ex.getMessage()
                    )
            );

            throw new ValidacaoException("Erro ao consultar template: " + ex.getMessage());
        }
    }

    void migrateTemplateInterno(Long systemUserId, boolean upgradeTenants) {
        boolean registrarLog = systemUserId != null;
        if (registrarLog) {
            salvarLog(
                    systemUserId,
                    "TEMPLATE_MIGRATION_STARTED",
                    "INFO",
                    "Inicio da migracao do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "host", properties.getHost(),
                            "port", properties.getPort(),
                            "location", "classpath:db/migration/template"
                    )
            );
        }

        try {
            templateRegistryService.acquireLock("MIGRATING");
            templateDatabaseAdminService.terminateConnections(properties.getDatabase());

            Flyway flyway = Flyway.configure()
                    .dataSource(
                            properties.buildJdbcUrl(),
                            properties.getUsername(),
                            properties.getPassword()
                    )
                    .locations("classpath:db/migration/template")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();

            MigrationInfo current = flyway.info().current();
            String currentVersion = current == null ? null : SchemaVersionUtils.fromFlywayVersion(current.getVersion().getVersion());
            templateRegistryService.markReady(currentVersion);

            if (registrarLog) {
                salvarLog(
                        systemUserId,
                        "TEMPLATE_MIGRATION_FINISHED",
                        "SUCESSO",
                        "Migracao do banco template executada com sucesso",
                        Map.of(
                                "database", properties.getDatabase(),
                                "host", properties.getHost(),
                                "port", properties.getPort(),
                                "location", "classpath:db/migration/template",
                                "currentVersion", currentVersion
                        )
                );
            }

            if (upgradeTenants) {
                tenantSchemaUpgradeService.upgradeOutdatedTenants(systemUserId, currentVersion);
            }
        } catch (Exception ex) {
            templateRegistryService.markError("ERROR");

            if (registrarLog) {
                salvarLog(
                        systemUserId,
                        "TEMPLATE_MIGRATION_FINISHED",
                        "ERRO",
                        "Erro ao executar migracao do banco template",
                        Map.of(
                                "database", properties.getDatabase(),
                                "host", properties.getHost(),
                                "port", properties.getPort(),
                                "location", "classpath:db/migration/template",
                                "erro", ex.getMessage()
                        )
                );
            }

            throw new ValidacaoException("Erro ao migrar template: " + ex.getMessage());
        }
    }

    private void salvarLog(
            Long systemUserId,
            String etapa,
            String status,
            String mensagem,
            Map<String, Object> detalhes
    ) {
        if (systemUserId == null) {
            return;
        }

        provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                null,
                etapa,
                status,
                mensagem,
                detalhes,
                systemUserId
        ));
    }
}
