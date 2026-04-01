package com.example.backend.master.platform.provisioningLogs;

import java.time.LocalDateTime;
import java.util.Map;

public record ProvisioningLogsRequestDTO
        (
                Long tenantId,
                String tenantCodigo,
                String tenantNome,
                String etapa,
                String status,
                String mensagem,
                Map<String, Object> detalhes,
                Long executadoPorId,
                String executadoPorLogin,
                String executadoPorNome,
                LocalDateTime createdAt
        ) {
}
