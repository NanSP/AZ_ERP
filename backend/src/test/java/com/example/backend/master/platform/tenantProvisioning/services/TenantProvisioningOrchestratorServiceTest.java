package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.master.platform.systemUsers.SystemUsers;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantProvisioningOrchestratorServiceTest {

    @Mock
    private TenantProvisioningRegistrationService registrationService;

    @Mock
    private TenantDatabaseProvisioningService tenantDatabaseProvisioningService;

    @Mock
    private TenantDatabasesService tenantDatabasesService;

    @Mock
    private TenantsService tenantsService;

    @Mock
    private TenantAdminUsersService tenantAdminUsersService;

    @Mock
    private ProvisioningLogsService provisioningLogsService;

    @InjectMocks
    private TenantProvisioningOrchestratorService service;

    @Test
    void deveProvisionarTenantComSucesso() {
        TenantProvisioningRequestDTO request = criarRequest();
        RegistrationResult registrationResult = criarRegistrationResult();
        Tenants tenantAtivo = criarTenant("ATIVO");
        TenantDatabases databaseAtiva = criarDatabase("ATIVO", LocalDateTime.of(2026, 5, 30, 11, 0));
        TenantAdminUsers tenantAdmin = criarTenantAdmin();

        when(registrationService.register(request)).thenReturn(registrationResult);
        when(tenantsService.atualizarStatusProvisionamento(1L, "ATIVO")).thenReturn(tenantAtivo);
        when(tenantDatabasesService.atualizarStatusProvisionamento(eq(2L), eq("ATIVO"), any(LocalDateTime.class)))
                .thenReturn(databaseAtiva);
        when(tenantAdminUsersService.criar(any(TenantAdminUsersRequestDTO.class))).thenReturn(tenantAdmin);

        TenantProvisioningResponseDTO response = service.provision(request);

        assertEquals(1L, response.tenantId());
        assertEquals("ATIVO", response.tenantStatus());
        assertEquals(2L, response.tenantDatabaseId());
        assertEquals("ATIVO", response.provisionStatus());
        assertEquals(3L, response.tenantAdminUserId());
        assertEquals(List.of("TENANT_CREATED", "DATABASE_REGISTERED", "DATABASE_CREATED", "TENANT_ADMIN_CREATED"),
                response.etapasExecutadas());

        ArgumentCaptor<TenantAdminUsersRequestDTO> adminCaptor = ArgumentCaptor.forClass(TenantAdminUsersRequestDTO.class);
        verify(tenantAdminUsersService).criar(adminCaptor.capture());
        assertEquals("TENANT_ADMIN", adminCaptor.getValue().role());

        verify(provisioningLogsService, times(2)).criar(any(ProvisioningLogsRequestDTO.class));
    }

    @Test
    void deveSuspenderTenantERegistrarErroQuandoProvisionamentoFisicoFalhar() {
        TenantProvisioningRequestDTO request = criarRequest();
        RegistrationResult registrationResult = criarRegistrationResult();
        Tenants tenantSuspenso = criarTenant("SUSPENSO");
        TenantDatabases databaseErro = criarDatabase("ERRO", null);

        when(registrationService.register(request)).thenReturn(registrationResult);
        doThrow(new TenantDatabaseProvisioningException("db error", new RuntimeException("boom")))
                .when(tenantDatabaseProvisioningService).createTenantDatabase("az_erp_tenant_1");
        when(tenantsService.atualizarStatusProvisionamento(1L, "SUSPENSO")).thenReturn(tenantSuspenso);
        when(tenantDatabasesService.atualizarStatusProvisionamento(eq(2L), eq("ERRO"), any(LocalDateTime.class)))
                .thenReturn(databaseErro);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.provision(request));

        assertEquals("Falha no provisionamento fisico do banco: db error", exception.getMessage());
        verify(tenantAdminUsersService, times(0)).criar(any(TenantAdminUsersRequestDTO.class));
        verify(provisioningLogsService).criar(any(ProvisioningLogsRequestDTO.class));
    }

    private TenantProvisioningRequestDTO criarRequest() {
        return new TenantProvisioningRequestDTO(
                99L,
                "TENANT-01",
                "Tenant 01",
                "Tenant Fantasia",
                "12345678000199",
                "CNPJ",
                "owner@tenant.com",
                "71999999999",
                "PROFESSIONAL",
                "az_erp_tenant_1",
                "localhost",
                5432,
                "postgres",
                "secret",
                "Admin Tenant",
                "admin@tenant.com",
                "tenant.admin",
                "senhaForte123"
        );
    }

    private RegistrationResult criarRegistrationResult() {
        SystemUsers executor = new SystemUsers();
        executor.setId(99L);

        return new RegistrationResult(
                criarTenant("PENDENTE"),
                criarDatabase("PENDENTE", null),
                executor,
                List.of("TENANT_CREATED", "DATABASE_REGISTERED")
        );
    }

    private Tenants criarTenant(String status) {
        Tenants tenant = new Tenants();
        tenant.setId(1L);
        tenant.setCodigo("TENANT-01");
        tenant.setNome("Tenant 01");
        tenant.setStatus(status);
        return tenant;
    }

    private TenantDatabases criarDatabase(String status, LocalDateTime provisionedAt) {
        TenantDatabases database = new TenantDatabases();
        database.setId(2L);
        database.setDatabaseName("az_erp_tenant_1");
        database.setProvisionStatus(status);
        database.setProvisionedAt(provisionedAt);
        return database;
    }

    private TenantAdminUsers criarTenantAdmin() {
        TenantAdminUsers admin = new TenantAdminUsers();
        admin.setId(3L);
        admin.setNome("Admin Tenant");
        admin.setEmail("admin@tenant.com");
        admin.setLogin("tenant.admin");
        return admin;
    }
}
