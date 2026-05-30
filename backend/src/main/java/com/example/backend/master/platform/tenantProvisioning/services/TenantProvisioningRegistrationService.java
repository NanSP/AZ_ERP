package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRequestDTO;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesService;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningRequestDTO;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRequestDTO;
import com.example.backend.master.platform.tenants.TenantsService;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TenantProvisioningRegistrationService {

    private final TenantsService tenantsService;
    private final TenantDatabasesService tenantDatabasesService;
    private final ProvisioningLogsService provisioningLogsService;
    private final SystemUsersRepository systemUsersRepository;

    public TenantProvisioningRegistrationService(
            TenantsService tenantsService,
            TenantDatabasesService tenantDatabasesService,
            ProvisioningLogsService provisioningLogsService,
            SystemUsersRepository systemUsersRepository
    ) {
        this.tenantsService = tenantsService;
        this.tenantDatabasesService = tenantDatabasesService;
        this.provisioningLogsService = provisioningLogsService;
        this.systemUsersRepository = systemUsersRepository;
    }

    @Transactional
    public RegistrationResult register(TenantProvisioningRequestDTO data) {
        List<String> etapasExecutadas = new ArrayList<>();

        SystemUsers executor = systemUsersRepository.findById(data.systemUserId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario executor nao encontrado"));

        Tenants tenant = tenantsService.criar(new TenantsRequestDTO(
                data.codigo(),
                data.nome(),
                data.nomeFantasia(),
                data.documento(),
                data.tipoDocumento(),
                data.emailResponsavel(),
                data.telefoneResponsavel(),
                "PENDENTE",
                data.plano(),
                "V1",
                null
        ));
        etapasExecutadas.add("TENANT_CREATED");

        provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                tenant.getId(),
                "TENANT_CREATED",
                "SUCESSO",
                "Tenant criado com sucesso",
                Map.of(
                        "tenantId", tenant.getId(),
                        "codigo", tenant.getCodigo(),
                        "nome", tenant.getNome()
                ),
                executor.getId()
        ));

        TenantDatabases tenantDatabase = tenantDatabasesService.criar(new TenantDatabasesRequestDTO(
                tenant.getId(),
                data.databaseName(),
                "az_erp_template",
                data.dbHost(),
                data.dbPort(),
                data.dbUsername(),
                data.dbPassword(),
                "PENDENTE",
                null
        ));
        etapasExecutadas.add("DATABASE_REGISTERED");

        provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                tenant.getId(),
                "DATABASE_REGISTERED",
                "SUCESSO",
                "Banco do tenant registrado com status PENDENTE",
                Map.of(
                        "tenantDatabaseId", tenantDatabase.getId(),
                        "databaseName", tenantDatabase.getDatabaseName(),
                        "status", tenantDatabase.getProvisionStatus()
                ),
                executor.getId()
        ));

        return new RegistrationResult(
                tenant,
                tenantDatabase,
                executor,
                etapasExecutadas
        );
    }
}
