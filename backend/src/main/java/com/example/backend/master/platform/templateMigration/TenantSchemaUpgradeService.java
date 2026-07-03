package com.example.backend.master.platform.templateMigration;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.master.platform.tenants.TenantsService;
import com.example.backend.security.SensitiveDataCipherService;
import com.example.backend.shared.db.PostgresJdbcUrlBuilder;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TenantSchemaUpgradeService {

    private final TenantsRepository tenantsRepository;
    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final TenantsService tenantsService;
    private final ProvisioningLogsService provisioningLogsService;
    private final SensitiveDataCipherService sensitiveDataCipherService;

    @Autowired
    public TenantSchemaUpgradeService(
            TenantsRepository tenantsRepository,
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsService tenantsService,
            ProvisioningLogsService provisioningLogsService,
            SensitiveDataCipherService sensitiveDataCipherService
    ) {
        this.tenantsRepository = tenantsRepository;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
        this.tenantsService = tenantsService;
        this.provisioningLogsService = provisioningLogsService;
        this.sensitiveDataCipherService = sensitiveDataCipherService;
    }

    public TenantSchemaUpgradeService(
            TenantsRepository tenantsRepository,
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsService tenantsService,
            ProvisioningLogsService provisioningLogsService
    ) {
        this(tenantsRepository, tenantDatabasesRepository, tenantsService, provisioningLogsService, null);
    }

    public void upgradeOutdatedTenants(Long systemUserId, String targetSchemaVersion) {
        List<Tenants> tenants = tenantsRepository.findAll();

        for (Tenants tenant : tenants) {
            if (!"ATIVO".equalsIgnoreCase(tenant.getStatus())) {
                continue;
            }

            if (SchemaVersionUtils.compare(tenant.getSchemaVersion(), targetSchemaVersion) >= 0) {
                continue;
            }

            tenantDatabasesRepository.findByTenantId(tenant)
                    .filter(database -> "ATIVO".equalsIgnoreCase(database.getProvisionStatus()))
                    .ifPresent(database -> migrarTenant(tenant, database, systemUserId, targetSchemaVersion));
        }
    }

    private void migrarTenant(
            Tenants tenant,
            TenantDatabases database,
            Long systemUserId,
            String targetSchemaVersion
    ) {
        salvarLog(
                tenant.getId(),
                systemUserId,
                "TENANT_SCHEMA_UPGRADE_STARTED",
                "INFO",
                "Inicio do upgrade incremental do tenant",
                Map.of(
                        "databaseName", database.getDatabaseName(),
                        "currentVersion", tenant.getSchemaVersion(),
                        "targetVersion", targetSchemaVersion
                )
        );

        try {
            Flyway.configure()
                    .dataSource(
                            buildJdbcUrl(database),
                            database.getDbUsername(),
                            decryptSensitive(database.getDbPassword())
                    )
                    .locations("classpath:db/migration/template")
                    .baselineOnMigrate(true)
                    .load()
                    .migrate();

            tenantsService.atualizarSchemaVersionInterna(tenant.getId(), targetSchemaVersion);

            salvarLog(
                    tenant.getId(),
                    systemUserId,
                    "TENANT_SCHEMA_UPGRADE_FINISHED",
                    "SUCESSO",
                    "Upgrade incremental do tenant concluido com sucesso",
                    Map.of(
                            "databaseName", database.getDatabaseName(),
                            "targetVersion", targetSchemaVersion
                    )
            );
        } catch (Exception ex) {
            salvarLog(
                    tenant.getId(),
                    systemUserId,
                    "TENANT_SCHEMA_UPGRADE_FINISHED",
                    "ERRO",
                    "Falha no upgrade incremental do tenant",
                    Map.of(
                            "databaseName", database.getDatabaseName(),
                            "targetVersion", targetSchemaVersion,
                            "erro", ex.getMessage()
                    )
            );
        }
    }

    private String buildJdbcUrl(TenantDatabases database) {
        return PostgresJdbcUrlBuilder.build(
                database.getDbHost(),
                database.getDbPort(),
                database.getDatabaseName()
        );
    }

    private void salvarLog(
            Long tenantId,
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
                tenantId,
                etapa,
                status,
                mensagem,
                detalhes,
                systemUserId
        ));
    }

    private String decryptSensitive(String value) {
        return sensitiveDataCipherService != null ? sensitiveDataCipherService.decrypt(value) : value;
    }
}
