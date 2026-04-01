package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import java.time.LocalDateTime;

public record TenantDatabasesResponseDTO
        (
                Long id,
                Tenants tenantId,
                String databaseName,
                String templateName,
                String dbHost,
                Integer dbPort,
                String dbUsername,
                LocalDateTime provisionedAt,
                String provisionStatus,
                LocalDateTime lastCheckAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        )
    {
        public TenantDatabasesResponseDTO(TenantDatabases tenantDatabases) {
            this
                    (
                            tenantDatabases.getId(),
                            tenantDatabases.getTenantId(),
                            tenantDatabases.getDatabaseName(),
                            tenantDatabases.getTemplateName(),
                            tenantDatabases.getDbHost(),
                            tenantDatabases.getDbPort(),
                            tenantDatabases.getDbUsername(),
                            tenantDatabases.getProvisionedAt(),
                            tenantDatabases.getProvisionStatus(),
                            tenantDatabases.getLastCheckAt(),
                            tenantDatabases.getCreatedAt(),
                            tenantDatabases.getUpdatedAt()
                    );
        }
}
