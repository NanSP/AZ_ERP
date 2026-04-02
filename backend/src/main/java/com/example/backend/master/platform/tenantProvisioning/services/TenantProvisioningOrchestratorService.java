package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogs;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningRequestDTO;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningResponseDTO;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service
public class TenantProvisioningOrchestratorService {

    private final TenantProvisioningRegistrationService registrationService;
    private final TenantDatabaseProvisioningService tenantDatabaseProvisioningService;
    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final TenantsRepository tenantsRepository;
    private final ProvisioningLogsRepository provisioningLogsRepository;

    public TenantProvisioningOrchestratorService(
            TenantProvisioningRegistrationService registrationService,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsRepository tenantsRepository,
            ProvisioningLogsRepository provisioningLogsRepository
    ) {
        this.registrationService = registrationService;
        this.tenantDatabaseProvisioningService = tenantDatabaseProvisioningService;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
        this.tenantsRepository = tenantsRepository;
        this.provisioningLogsRepository = provisioningLogsRepository;
    }

    public TenantProvisioningResponseDTO provision(TenantProvisioningRequestDTO data) {
        RegistrationResult result = registrationService.register(data);

        TenantDatabases tenantDatabase = result.tenantDatabase();
        LocalDateTime now = LocalDateTime.now();
        ArrayList<String> etapasExecutadas = new ArrayList<>(result.etapasExecutadas());

        try {
            tenantDatabaseProvisioningService.createTenantDatabase(
                    tenantDatabase.getDatabaseName()
            );

            result.tenant().setStatus("ATIVO");
            result.tenant().setUpdatedAt(now);
            tenantsRepository.save(result.tenant());

            tenantDatabase.setProvisionStatus("ATIVO");
            tenantDatabase.setProvisionedAt(now);
            tenantDatabase.setUpdatedAt(now);
            tenantDatabasesRepository.save(tenantDatabase);

            etapasExecutadas.add("DATABASE_CREATED");

            salvarLogFinal(
                    result,
                    "DATABASE_CREATED",
                    "SUCESSO",
                    "Banco físico do tenant criado com sucesso",
                    Map.of(
                            "tenantDatabaseId", tenantDatabase.getId(),
                            "databaseName", tenantDatabase.getDatabaseName(),
                            "status", tenantDatabase.getProvisionStatus()
                    ),
                    now
            );

        } catch (Exception ex) {

            result.tenant().setStatus("SUSPENSO");
            result.tenant().setUpdatedAt(now);
            tenantsRepository.save(result.tenant());

            tenantDatabase.setProvisionStatus("ERRO");
            tenantDatabase.setUpdatedAt(now);
            tenantDatabasesRepository.save(tenantDatabase);

            etapasExecutadas.add("DATABASE_ERROR");

            salvarLogFinal(
                    result,
                    "DATABASE_CREATED",
                    "ERRO",
                    "Erro ao criar banco físico do tenant",
                    Map.of(
                            "tenantDatabaseId", tenantDatabase.getId(),
                            "databaseName", tenantDatabase.getDatabaseName(),
                            "erro", ex.getMessage()
                    ),
                    now
            );

            throw new RuntimeException(
                    "Falha no provisionamento físico do banco: " + ex.getMessage(),
                    ex
            );
        }

        return new TenantProvisioningResponseDTO(
                result.tenant().getId(),
                result.tenant().getCodigo(),
                result.tenant().getNome(),
                result.tenant().getStatus(),
                tenantDatabase.getId(),
                tenantDatabase.getDatabaseName(),
                tenantDatabase.getProvisionStatus(),
                result.tenantAdmin().getId(),
                result.tenantAdmin().getNome(),
                result.tenantAdmin().getEmail(),
                result.tenantAdmin().getLogin(),
                tenantDatabase.getProvisionedAt(),
                etapasExecutadas
        );
    }

    private void salvarLogFinal(
            RegistrationResult result,
            String etapa,
            String status,
            String mensagem,
            Map<String, Object> detalhes,
            LocalDateTime createdAt
    ) {
        ProvisioningLogs log = new ProvisioningLogs();
        log.setTenantId(result.tenant());
        log.setExecutadoPor(result.executor());
        log.setEtapa(etapa);
        log.setStatus(status);
        log.setMensagem(mensagem);
        log.setDetalhes(detalhes);
        log.setCreatedAt(createdAt);

        provisioningLogsRepository.save(log);
    }
}
