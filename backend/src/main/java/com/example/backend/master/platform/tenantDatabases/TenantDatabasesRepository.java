package com.example.backend.master.platform.tenantDatabases;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantDatabasesRepository extends JpaRepository<TenantDatabases, Long> {
    boolean existsByDatabaseNameIgnoreCase(String databaseName);
}
