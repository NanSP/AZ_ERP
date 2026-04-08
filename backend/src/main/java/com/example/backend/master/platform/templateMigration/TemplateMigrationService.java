package com.example.backend.master.platform.templateMigration;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogs;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TemplateMigrationService {

    private final TemplateMigrationProperties properties;
    private final ProvisioningLogsRepository provisioningLogsRepository;
    private final SystemUsersRepository systemUsersRepository;

    public TemplateMigrationService(
            TemplateMigrationProperties properties,
            ProvisioningLogsRepository provisioningLogsRepository,
            SystemUsersRepository systemUsersRepository
    ) {
        this.properties = properties;
        this.provisioningLogsRepository = provisioningLogsRepository;
        this.systemUsersRepository = systemUsersRepository;
    }

    public void migrateTemplate(Long systemUserId) {
        SystemUsers executor = systemUsersRepository.findById(systemUserId)
                .orElseThrow(() -> new RuntimeException("Usuario executor nao encontrado"));

        LocalDateTime now = LocalDateTime.now();

        salvarLog(
                executor,
                "TEMPLATE_MIGRATION_STARTED",
                "INFO",
                "Inicio da migracao do banco template",
                Map.of(
                        "database", properties.getDatabase(),
                        "host", properties.getHost(),
                        "port", properties.getPort(),
                        "location", "classpath:db/migration/template"
                ),
                now
        );

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

            flyway.migrate();

            salvarLog(
                    executor,
                    "TEMPLATE_MIGRATION_FINISHED",
                    "SUCESSO",
                    "Migracao do banco template executada com sucesso",
                    Map.of(
                            "database", properties.getDatabase(),
                            "host", properties.getHost(),
                            "port", properties.getPort(),
                            "location", "classpath:db/migration/template"
                    ),
                    LocalDateTime.now()
            );

        } catch (Exception ex) {
            salvarLog(
                    executor,
                    "TEMPLATE_MIGRATION_FINISHED",
                    "ERRO",
                    "Erro ao executar migracao do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "host", properties.getHost(),
                            "port", properties.getPort(),
                            "location", "classpath:db/migration/template",
                            "erro", ex.getMessage()
                    ),
                    LocalDateTime.now()
            );

            throw new RuntimeException("Erro ao migrar template: " + ex.getMessage(), ex);
        }
    }

    public void validateTemplate(Long systemUserId) {
        SystemUsers executor = systemUsersRepository.findById(systemUserId)
                .orElseThrow(() -> new RuntimeException("Usuario executor nao encontrado"));

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

            flyway.validate();

            salvarLog(
                    executor,
                    "TEMPLATE_VALIDATION",
                    "SUCESSO",
                    "Validacao do banco template executada com sucesso",
                    Map.of(
                            "database", properties.getDatabase(),
                            "location", "classpath:db/migration/template"
                    ),
                    LocalDateTime.now()
            );

        } catch (Exception ex) {
            salvarLog(
                    executor,
                    "TEMPLATE_VALIDATION",
                    "ERRO",
                    "Erro na validacao do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "location", "classpath:db/migration/template",
                            "erro", ex.getMessage()
                    ),
                    LocalDateTime.now()
            );

            throw new RuntimeException("Erro ao validar template: " + ex.getMessage(), ex);
        }
    }

    public String infoTemplate(Long systemUserId) {
        SystemUsers executor = systemUsersRepository.findById(systemUserId)
                .orElseThrow(() -> new RuntimeException("Usuario executor nao encontrado"));

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

            var current = flyway.info().current();
            String info = current == null
                    ? "Template sem versao aplicada"
                    : "Versao atual do template: " + current.getVersion() + " - " + current.getDescription();

            salvarLog(
                    executor,
                    "TEMPLATE_INFO",
                    "INFO",
                    "Consulta de versao do banco template executada",
                    Map.of(
                            "database", properties.getDatabase(),
                            "info", info
                    ),
                    LocalDateTime.now()
            );

            return info;

        } catch (Exception ex) {
            salvarLog(
                    executor,
                    "TEMPLATE_INFO",
                    "ERRO",
                    "Erro ao consultar informacoes do banco template",
                    Map.of(
                            "database", properties.getDatabase(),
                            "erro", ex.getMessage()
                    ),
                    LocalDateTime.now()
            );

            throw new RuntimeException("Erro ao consultar template: " + ex.getMessage(), ex);
        }
    }

    private void salvarLog(
            SystemUsers executor,
            String etapa,
            String status,
            String mensagem,
            Map<String, Object> detalhes,
            LocalDateTime createdAt
    ) {
        ProvisioningLogs log = new ProvisioningLogs();
        log.setTenantId(null);
        log.setExecutadoPor(executor);
        log.setEtapa(etapa);
        log.setStatus(status);
        log.setMensagem(mensagem);
        log.setDetalhes(detalhes);
        log.setCreatedAt(createdAt);

        provisioningLogsRepository.save(log);
    }
}
