package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantDatabasesRepository extends JpaRepository<TenantDatabases, Long> {
    boolean existsByDatabaseNameIgnoreCase(String databaseName);
    boolean existsByDatabaseNameIgnoreCaseAndIdNot(String databaseName, Long id);
    Optional<TenantDatabases> findByTenantId(Tenants tenant);
    boolean existsByTenantIdId(Long tenantId);
}
