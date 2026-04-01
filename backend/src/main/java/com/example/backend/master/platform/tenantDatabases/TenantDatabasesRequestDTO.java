package com.example.backend.master.platform.tenantDatabases;

import java.time.LocalDateTime;

public record TenantDatabasesRequestDTO
        (
                Long tenantId,
                String tenantCodigo,
                String tenantNome,
                String databaseName,
                String templateName,
                String dbHost,
                Integer dbPort,
                String dbUsername,
                String dbPasswordEncrypted,
                LocalDateTime provisionedAt,
                String provisionStatus,
                LocalDateTime lastCheckAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        ) {
}
