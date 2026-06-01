package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsers;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsersRequestDTO;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsersService;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesService;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningRequestDTO;
import com.example.backend.master.platform.tenantProvisioning.TenantProvisioningResponseDTO;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsService;
import com.example.backend.shared.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service
public class TenantProvisioningOrchestratorService {

    private final TenantProvisioningRegistrationService registrationService;
    private final TenantDatabaseProvisioningService tenantDatabaseProvisioningService;
    private final TenantDatabasesService tenantDatabasesService;
    private final TenantsService tenantsService;
    private final TenantAdminUsersService tenantAdminUsersService;
    private final TenantApplicationUserProvisioningService tenantApplicationUserProvisioningService;
    private final ProvisioningLogsService provisioningLogsService;

    public TenantProvisioningOrchestratorService(
            TenantProvisioningRegistrationService registrationService,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantDatabasesService tenantDatabasesService,
            TenantsService tenantsService,
            TenantAdminUsersService tenantAdminUsersService,
            TenantApplicationUserProvisioningService tenantApplicationUserProvisioningService,
            ProvisioningLogsService provisioningLogsService
    ) {
        this.registrationService = registrationService;
        this.tenantDatabaseProvisioningService = tenantDatabaseProvisioningService;
        this.tenantDatabasesService = tenantDatabasesService;
        this.tenantsService = tenantsService;
        this.tenantAdminUsersService = tenantAdminUsersService;
        this.tenantApplicationUserProvisioningService = tenantApplicationUserProvisioningService;
        this.provisioningLogsService = provisioningLogsService;
    }

    public TenantProvisioningResponseDTO provision(TenantProvisioningRequestDTO data) {
        RegistrationResult result = registrationService.register(data);

        TenantDatabases tenantDatabase = result.tenantDatabase();
        Tenants tenant = result.tenant();
        ArrayList<String> etapasExecutadas = new ArrayList<>(result.etapasExecutadas());
        TenantAdminUsers tenantAdmin;

        try {
            tenantDatabaseProvisioningService.createTenantDatabase(tenantDatabase.getDatabaseName());

            tenant = tenantsService.atualizarStatusProvisionamento(tenant.getId(), "ATIVO");
            tenantDatabase = tenantDatabasesService.atualizarStatusProvisionamento(
                    tenantDatabase.getId(),
                    "ATIVO",
                    LocalDateTime.now()
            );
            etapasExecutadas.add("DATABASE_CREATED");

            provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                    tenant.getId(),
                    "DATABASE_CREATED",
                    "SUCESSO",
                    "Banco fisico do tenant criado com sucesso",
                    Map.of(
                            "tenantDatabaseId", tenantDatabase.getId(),
                            "databaseName", tenantDatabase.getDatabaseName(),
                            "status", tenantDatabase.getProvisionStatus()
                    ),
                    result.executor().getId()
            ));

            tenantApplicationUserProvisioningService.createInitialAdminUser(
                    tenantDatabase,
                    data.adminNome(),
                    data.adminEmail(),
                    data.adminLogin(),
                    data.adminSenha()
            );
            etapasExecutadas.add("TENANT_APP_USER_CREATED");

            provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                    tenant.getId(),
                    "TENANT_APP_USER_CREATED",
                    "SUCESSO",
                    "Usuario inicial do tenant criado no banco provisionado",
                    Map.of(
                            "databaseName", tenantDatabase.getDatabaseName(),
                            "adminEmail", data.adminEmail(),
                            "adminLogin", data.adminLogin()
                    ),
                    result.executor().getId()
            ));

            tenantAdmin = tenantAdminUsersService.criar(new TenantAdminUsersRequestDTO(
                    tenant.getId(),
                    data.adminNome(),
                    data.adminEmail(),
                    data.adminLogin(),
                    data.adminSenha(),
                    "TENANT_ADMIN",
                    "ATIVO"
            ));
            etapasExecutadas.add("TENANT_ADMIN_CREATED");

            provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                    tenant.getId(),
                    "TENANT_ADMIN_CREATED",
                    "SUCESSO",
                    "Administrador inicial do tenant criado com sucesso",
                    Map.of(
                            "tenantAdminUserId", tenantAdmin.getId(),
                            "adminNome", tenantAdmin.getNome(),
                            "adminEmail", tenantAdmin.getEmail(),
                            "adminLogin", tenantAdmin.getLogin()
                    ),
                    result.executor().getId()
            ));
        } catch (Exception ex) {
            tenant = tenantsService.atualizarStatusProvisionamento(tenant.getId(), "SUSPENSO");
            tenantDatabase = tenantDatabasesService.atualizarStatusProvisionamento(
                    tenantDatabase.getId(),
                    "ERRO",
                    LocalDateTime.now()
            );
            etapasExecutadas.add("DATABASE_ERROR");

            provisioningLogsService.criar(new ProvisioningLogsRequestDTO(
                    tenant.getId(),
                    "DATABASE_CREATED",
                    "ERRO",
                    "Erro ao criar banco fisico do tenant",
                    Map.of(
                            "tenantDatabaseId", tenantDatabase.getId(),
                            "databaseName", tenantDatabase.getDatabaseName(),
                            "erro", ex.getMessage()
                    ),
                    result.executor().getId()
            ));

            throw new ValidacaoException("Falha no provisionamento fisico do banco: " + ex.getMessage());
        }

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
                tenantDatabase.getProvisionedAt(),
                etapasExecutadas
        );
    }
}
