package com.example.backend.tenant.context;

import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantConnectionService {

    private final TenantsRepository tenantsRepository;
    private final TenantDatabasesRepository tenantDatabasesRepository;

    public TenantConnectionService(
            TenantsRepository tenantsRepository,
            TenantDatabasesRepository tenantDatabasesRepository
    ) {
        this.tenantsRepository = tenantsRepository;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
    }

    public TenantConnectionInfo resolve(String tenantCode) {
        Tenants tenant = tenantsRepository.findByCodigoIgnoreCase(tenantCode)
                .orElseThrow(() -> new RuntimeException("Tenant nao encontrado"));

        TenantDatabases tenantDatabase = tenantDatabasesRepository.findByTenantId(tenant)
                .orElseThrow(() -> new RuntimeException("Banco do tenant nao encontrado"));

        if (!"ATIVO".equalsIgnoreCase(tenant.getStatus())) {
            throw new RuntimeException("Tenant inativo");
        }

        if (!"ATIVO".equalsIgnoreCase(tenantDatabase.getProvisionStatus())) {
            throw new RuntimeException("Banco do tenant nao esta ativo");
        }

        return new TenantConnectionInfo(
                tenant.getId(),
                tenant.getCodigo(),
                tenantDatabase.getDatabaseName(),
                tenantDatabase.getDbHost(),
                tenantDatabase.getDbPort(),
                tenantDatabase.getDbUsername(),
                tenantDatabase.getDbPassword()
        );
    }
}

