package com.example.backend.master.platform.tenantProvisioning;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogs;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsers;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsersRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TenantProvisioningService {

    private final TenantsRepository tenantsRepository;
    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final TenantAdminUsersRepository tenantAdminUsersRepository;
    private final ProvisioningLogsRepository provisioningLogsRepository;
    private final SystemUsersRepository systemUsersRepository;

    public TenantProvisioningService(
            TenantsRepository tenantsRepository,
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantAdminUsersRepository tenantAdminUsersRepository,
            ProvisioningLogsRepository provisioningLogsRepository,
            SystemUsersRepository systemUsersRepository
    ) {
        this.tenantsRepository = tenantsRepository;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
        this.tenantAdminUsersRepository = tenantAdminUsersRepository;
        this.provisioningLogsRepository = provisioningLogsRepository;
        this.systemUsersRepository = systemUsersRepository;
    }

    @Transactional
    public TenantProvisioningResponseDTO provision(TenantProvisioningRequestDTO data) {
        List<String> etapasExecutadas = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        Tenants tenant = new Tenants();
        tenant.setCodigo(data.codigo());
        tenant.setNome(data.nome());
        tenant.setNomeFantasia(data.nomeFantasia());
        tenant.setDocumento(data.documento());
        tenant.setTipoDocumento(data.tipoDocumento());
        tenant.setEmailResponsavel(data.emailResponsavel());
        tenant.setTelefoneResponsavel(data.telefoneResponsavel());
        tenant.setPlano(data.plano());
        tenant.setStatus("PENDENTE");
        tenant.setSchemaVersion("v1");
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenant = tenantsRepository.save(tenant);
        etapasExecutadas.add("TENANT_CREATED");

        TenantDatabases tenantDatabase = new TenantDatabases();
        tenantDatabase.setTenantId(tenant);
        tenantDatabase.setDatabaseName(data.databaseName());
        tenantDatabase.setTemplateName("az_erp_template");
        tenantDatabase.setDbHost(data.dbHost());
        tenantDatabase.setDbPort(data.dbPort());
        tenantDatabase.setDbUsername(data.dbUsername());
        tenantDatabase.setDbPasswordEncrypted(data.dbPasswordEncrypted());
        tenantDatabase.setProvisionStatus("PENDENTE");
        tenantDatabase.setProvisionedAt(null);
        tenantDatabase.setLastCheckAt(null);
        tenantDatabase.setCreatedAt(now);
        tenantDatabase.setUpdatedAt(now);
        tenantDatabase = tenantDatabasesRepository.save(tenantDatabase);
        etapasExecutadas.add("DATABASE_REGISTERED");

        TenantAdminUsers tenantAdmin = new TenantAdminUsers();
        tenantAdmin.setTenantId(tenant);
        tenantAdmin.setNome(data.adminNome());
        tenantAdmin.setEmail(data.adminEmail());
        tenantAdmin.setLogin(data.adminLogin());
        tenantAdmin.setSenhaHash(data.adminSenhaHash());
        tenantAdmin.setRole("ADMIN_TENANT");
        tenantAdmin.setStatus("ATIVO");
        tenantAdmin.setUltimoAcesso(null);
        tenantAdmin.setCreatedAt(now);
        tenantAdmin.setUpdatedAt(now);
        tenantAdmin = tenantAdminUsersRepository.save(tenantAdmin);
        etapasExecutadas.add("TENANT_ADMIN_CREATED");

        SystemUsers executor = systemUsersRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        ProvisioningLogs logTenant = new ProvisioningLogs();
        logTenant.setTenantId(tenant);
        logTenant.setEtapa("TENANT_CREATED");
        logTenant.setStatus("SUCESSO");
        logTenant.setMensagem("Tenant criado com sucesso");
        logTenant.setDetalhes(Map.of(
                "tenantId", tenant.getId(),
                "codigo", tenant.getCodigo()
        ));
        logTenant.setExecutadoPor(executor);
        logTenant.setCreatedAt(now);
        provisioningLogsRepository.save(logTenant);

        ProvisioningLogs logDatabase = new ProvisioningLogs();
        logDatabase.setTenantId(tenant);
        logDatabase.setEtapa("DATABASE_REGISTERED");
        logDatabase.setStatus("SUCESSO");
        logDatabase.setMensagem("Banco do tenant registrado com sucesso");
        logDatabase.setDetalhes(Map.of(
                "tenantDatabaseId", tenantDatabase.getId(),
                "databaseName", tenantDatabase.getDatabaseName()
        ));
        logDatabase.setExecutadoPor(executor);
        logDatabase.setCreatedAt(now);
        provisioningLogsRepository.save(logDatabase);

        ProvisioningLogs logAdmin = new ProvisioningLogs();
        logAdmin.setTenantId(tenant);
        logAdmin.setEtapa("TENANT_ADMIN_CREATED");
        logAdmin.setStatus("SUCESSO");
        logAdmin.setMensagem("Administrador inicial do tenant criado com sucesso");
        logAdmin.setDetalhes(Map.of(
                "tenantAdminUserId", tenantAdmin.getId(),
                "adminLogin", tenantAdmin.getLogin()
        ));
        logAdmin.setExecutadoPor(executor);
        logAdmin.setCreatedAt(now);
        provisioningLogsRepository.save(logAdmin);

        return new TenantProvisioningResponseDTO(
                tenant.getId(),
                tenant.getCodigo(),
                tenant.getNome(),
                tenant.getStatus(),
                tenantDatabase.getId(),
                tenantDatabase.getDatabaseName(),
                tenantDatabase.getProvisionStatus(),
                tenantAdmin.getId(),
                tenantAdmin.getNome(),
                tenantAdmin.getEmail(),
                tenantAdmin.getLogin(),
                now,
                etapasExecutadas
        );
    }
}
