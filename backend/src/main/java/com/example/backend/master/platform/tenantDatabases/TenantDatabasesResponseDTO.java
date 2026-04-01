package com.example.backend.master.platform.tenantDatabases;

import java.time.LocalDateTime;

public record TenantDatabasesResponseDTO
        (
                Long id,
                Long tenantId,
                String tenantCodigo,
                String tenantNome,
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
                            tenantDatabases.getTenantId() != null ? tenantDatabases.getTenantId().getId() : null,
                            tenantDatabases.getTenantId() != null ? tenantDatabases.getTenantId().getCodigo() : null,
                            tenantDatabases.getTenantId() != null ? tenantDatabases.getTenantId().getNome() : null,
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
