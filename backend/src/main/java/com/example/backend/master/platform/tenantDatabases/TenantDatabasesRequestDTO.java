package com.example.backend.master.platform.tenantDatabases;

import java.time.LocalDateTime;

public record TenantDatabasesRequestDTO
        (
                Long tenantId,
                String databaseName,
                String templateName,
                String dbHost,
                Integer dbPort,
                String dbUsername,
                String dbPassword,
                String provisionStatus,
                LocalDateTime lastCheckAt
        ) {
}
