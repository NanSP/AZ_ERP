package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.tenants.Tenants;

import java.time.LocalDateTime;
import java.util.Map;

public record ProvisioningLogsRequestDTO
        (
                Tenants tenantId,
                String etapa,
                String status,
                String mensagem,
                Map<String, Object> detalhes,
                SystemUsers executadoPor,
                LocalDateTime createdAt
        ) {
}
