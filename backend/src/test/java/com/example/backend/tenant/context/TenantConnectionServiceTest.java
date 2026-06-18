package com.example.backend.tenant.context;

import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantConnectionServiceTest {

    @Mock
    private TenantsRepository tenantsRepository;
    @Mock
    private TenantDatabasesRepository tenantDatabasesRepository;

    @InjectMocks
    private TenantConnectionService service;

    @Test
    void deveResolverConexaoDeTenantAtivo() {
        Tenants tenant = criarTenant(1L, "TENANT_A", "ATIVO");
        TenantDatabases tenantDatabase = criarTenantDatabase(tenant, "ATIVO");

        when(tenantsRepository.findByCodigo("TENANT_A")).thenReturn(Optional.of(tenant));
        when(tenantDatabasesRepository.findByTenantId(tenant)).thenReturn(Optional.of(tenantDatabase));

        TenantConnectionInfo info = service.resolve("TENANT_A");

        assertEquals(1L, info.tenantId());
        assertEquals("TENANT_A", info.tenantCode());
        assertEquals("jdbc:postgresql://localhost:5432/tenant_a_db?sslmode=require", info.jdbcUrl());
    }

    @Test
    void deveBloquearTenantInativo() {
        Tenants tenant = criarTenant(1L, "TENANT_A", "INATIVO");
        TenantDatabases tenantDatabase = criarTenantDatabase(tenant, "ATIVO");

        when(tenantsRepository.findByCodigo("TENANT_A")).thenReturn(Optional.of(tenant));
        when(tenantDatabasesRepository.findByTenantId(tenant)).thenReturn(Optional.of(tenantDatabase));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.resolve("TENANT_A"));

        assertEquals("Tenant inativo", exception.getMessage());
    }

    @Test
    void deveBloquearBancoDeTenantNaoAtivo() {
        Tenants tenant = criarTenant(1L, "TENANT_A", "ATIVO");
        TenantDatabases tenantDatabase = criarTenantDatabase(tenant, "PENDENTE");

        when(tenantsRepository.findByCodigo("TENANT_A")).thenReturn(Optional.of(tenant));
        when(tenantDatabasesRepository.findByTenantId(tenant)).thenReturn(Optional.of(tenantDatabase));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.resolve("TENANT_A"));

        assertEquals("Banco do tenant nao esta ativo", exception.getMessage());
    }

    private Tenants criarTenant(Long id, String codigo, String status) {
        Tenants tenant = new Tenants();
        tenant.setId(id);
        tenant.setCodigo(codigo);
        tenant.setStatus(status);
        return tenant;
    }

    private TenantDatabases criarTenantDatabase(Tenants tenant, String status) {
        TenantDatabases tenantDatabase = new TenantDatabases();
        tenantDatabase.setTenantId(tenant);
        tenantDatabase.setDatabaseName("tenant_a_db");
        tenantDatabase.setDbHost("localhost");
        tenantDatabase.setDbPort(5432);
        tenantDatabase.setDbUsername("user");
        tenantDatabase.setDbPassword("pass");
        tenantDatabase.setProvisionStatus(status);
        return tenantDatabase;
    }
}
