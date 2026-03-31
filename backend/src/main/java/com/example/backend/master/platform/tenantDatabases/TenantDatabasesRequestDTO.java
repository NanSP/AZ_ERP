package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;

import java.time.LocalDateTime;

public record TenantDatabasesRequestDTO
        (
                Tenants tenantId,
                String databaseName,
                String templateName,
                String dbHost,
                Integer dbPort,
                String dbUsername,
                String dbPasswordEncrypted,
                String provisionedAt,
                String provisionStatus,
                LocalDateTime lastCheckAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        ) {
}
