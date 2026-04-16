package com.example.backend.tenant.context;

import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantDataSourceRegistry {

    private final TenantsRepository tenantsRepository;
    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    public TenantDataSourceRegistry(
            TenantsRepository tenantsRepository,
            TenantDatabasesRepository tenantDatabasesRepository
    ) {
        this.tenantsRepository = tenantsRepository;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
    }

    public DataSource getOrCreate(String tenantCode) {
        return cache.computeIfAbsent(tenantCode, this::createDataSource);
    }

    private DataSource createDataSource(String tenantCode) {
        Tenants tenant = tenantsRepository.findByCodigoIgnoreCase(tenantCode)
                .orElseThrow(() -> new RuntimeException("Tenant nao encontrado"));

        TenantDatabases db = tenantDatabasesRepository.findByTenantId(tenant)
                .orElseThrow(() -> new RuntimeException("Banco do tenant nao encontrado"));

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://" + db.getDbHost() + ":" + db.getDbPort() + "/" + db.getDatabaseName());
        dataSource.setUsername(db.getDbUsername());
        dataSource.setPassword(db.getDbPassword());
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }
}
