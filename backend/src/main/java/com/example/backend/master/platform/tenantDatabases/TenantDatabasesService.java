package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantDatabasesService {

    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final TenantsRepository tenantsRepository;

    public TenantDatabasesService(
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsRepository tenantsRepository
    ) {
        this.tenantDatabasesRepository = tenantDatabasesRepository;
        this.tenantsRepository = tenantsRepository;
    }

    public TenantDatabases create(TenantDatabasesRequestDTO data) {
        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        TenantDatabases entity = new TenantDatabases();
        entity.setTenantId(tenant);
        entity.setDatabaseName(data.databaseName());
        entity.setTemplateName(data.templateName());
        entity.setDbHost(data.dbHost());
        entity.setDbPort(data.dbPort());
        entity.setDbUsername(data.dbUsername());
        entity.setDbPasswordEncrypted(data.dbPasswordEncrypted());
        entity.setProvisionedAt(data.provisionedAt());
        entity.setProvisionStatus(data.provisionStatus());
        entity.setLastCheckAt(data.lastCheckAt());
        entity.setCreatedAt(data.createdAt());
        entity.setUpdatedAt(data.updatedAt());

        return tenantDatabasesRepository.save(entity);
    }

    public TenantDatabases update(Long id, TenantDatabasesRequestDTO data) {
        TenantDatabases entity = tenantDatabasesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant database não encontrado"));

        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        entity.setTenantId(tenant);
        entity.setDatabaseName(data.databaseName());
        entity.setTemplateName(data.templateName());
        entity.setDbHost(data.dbHost());
        entity.setDbPort(data.dbPort());
        entity.setDbUsername(data.dbUsername());
        entity.setDbPasswordEncrypted(data.dbPasswordEncrypted());
        entity.setProvisionedAt(data.provisionedAt());
        entity.setProvisionStatus(data.provisionStatus());
        entity.setLastCheckAt(data.lastCheckAt());
        entity.setCreatedAt(data.createdAt());
        entity.setUpdatedAt(data.updatedAt());

        return tenantDatabasesRepository.save(entity);
    }
}
