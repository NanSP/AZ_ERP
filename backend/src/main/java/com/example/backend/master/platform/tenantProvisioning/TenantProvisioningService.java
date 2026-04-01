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
        validarDuplicidades(data);

        LocalDateTime now = LocalDateTime.now();
        List<String> etapasExecutadas = new ArrayList<>();

        SystemUsers executor = systemUsersRepository.findById(data.systemUserId())
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado"));

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

        salvarLog(
                tenant,
                executor,
                "TENANT_CREATED",
                "SUCESSO",
                "Tenant criado com sucesso",
                Map.of(
                        "tenantId", tenant.getId(),
                        "codigo", tenant.getCodigo(),
                        "nome", tenant.getNome()
                ),
                now
        );

        TenantDatabases tenantDatabase = new TenantDatabases();
        tenantDatabase.setTenantId(tenant);
        tenantDatabase.setDatabaseName(data.databaseName());
        tenantDatabase.setTemplateName("az_erp_template");
        tenantDatabase.setDbHost(data.dbHost());
        tenantDatabase.setDbPort(data.dbPort());
        tenantDatabase.setDbUsername(data.dbUsername());
        tenantDatabase.setDbPassword(data.dbPassword());
        tenantDatabase.setProvisionStatus("PENDENTE");
        tenantDatabase.setProvisionedAt(null);
        tenantDatabase.setLastCheckAt(null);
        tenantDatabase.setCreatedAt(now);
        tenantDatabase.setUpdatedAt(now);
        tenantDatabase = tenantDatabasesRepository.save(tenantDatabase);
        etapasExecutadas.add("DATABASE_REGISTERED");

        salvarLog(
                tenant,
                executor,
                "DATABASE_REGISTERED",
                "SUCESSO",
                "Banco do tenant registrado com sucesso",
                Map.of(
                        "tenantDatabaseId", tenantDatabase.getId(),
                        "databaseName", tenantDatabase.getDatabaseName(),
                        "dbHost", tenantDatabase.getDbHost(),
                        "dbPort", tenantDatabase.getDbPort()
                ),
                now
        );

        TenantAdminUsers tenantAdmin = new TenantAdminUsers();
        tenantAdmin.setTenantId(tenant);
        tenantAdmin.setNome(data.adminNome());
        tenantAdmin.setEmail(data.adminEmail());
        tenantAdmin.setLogin(data.adminLogin());
        tenantAdmin.setSenha(data.adminSenha());
        tenantAdmin.setRole("ADMIN_TENANT");
        tenantAdmin.setStatus("ATIVO");
        tenantAdmin.setUltimoAcesso(null);
        tenantAdmin.setCreatedAt(now);
        tenantAdmin.setUpdatedAt(now);
        tenantAdmin = tenantAdminUsersRepository.save(tenantAdmin);
        etapasExecutadas.add("TENANT_ADMIN_CREATED");

        salvarLog(
                tenant,
                executor,
                "TENANT_ADMIN_CREATED",
                "SUCESSO",
                "Administrador inicial do tenant criado com sucesso",
                Map.of(
                        "tenantAdminUserId", tenantAdmin.getId(),
                        "adminNome", tenantAdmin.getNome(),
                        "adminEmail", tenantAdmin.getEmail(),
                        "adminLogin", tenantAdmin.getLogin()
                ),
                now
        );

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

    private void validarDuplicidades(TenantProvisioningRequestDTO data) {
        if (tenantsRepository.existsByCodigoIgnoreCase(data.codigo())) {
            throw new RuntimeException("Já existe um tenant com esse código");
        }

        if (tenantDatabasesRepository.existsByDatabaseNameIgnoreCase(data.databaseName())) {
            throw new RuntimeException("Já existe um banco cadastrado com esse nome");
        }

        if (tenantAdminUsersRepository.existsByEmailIgnoreCase(data.adminEmail())) {
            throw new RuntimeException("Já existe um admin de tenant com esse email");
        }

        if (tenantAdminUsersRepository.existsByLoginIgnoreCase(data.adminLogin())) {
            throw new RuntimeException("Já existe um admin de tenant com esse login");
        }
    }

    private void salvarLog(
            Tenants tenant,
            SystemUsers executor,
            String etapa,
            String status,
            String mensagem,
            Map<String, Object> detalhes,
            LocalDateTime createdAt
    ) {
        ProvisioningLogs log = new ProvisioningLogs();
        log.setTenantId(tenant);
        log.setEtapa(etapa);
        log.setStatus(status);
        log.setMensagem(mensagem);
        log.setDetalhes(detalhes);
        log.setExecutadoPor(executor);
        log.setCreatedAt(createdAt);

        provisioningLogsRepository.save(log);
    }
}
