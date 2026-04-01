package com.example.backend.master.platform.tenantProvisioning;

import java.time.LocalDateTime;
import java.util.List;

public record TenantProvisioningResponseDTO(
        Long tenantId,
        String tenantCodigo,
        String tenantNome,
        String tenantStatus,
        Long tenantDatabaseId,
        String databaseName,
        String provisionStatus,
        Long tenantAdminUserId,
        String adminNome,
        String adminEmail,
        String adminLogin,
        LocalDateTime provisionedAt,
        List<String> etapasExecutadas
) {
}

