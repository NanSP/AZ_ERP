package com.example.backend.master.platform.provisioningLogs;

import java.time.LocalDateTime;
import java.util.Map;

public record ProvisioningLogsResponseDTO
        (
                Long id,
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
        )
    {
        public ProvisioningLogsResponseDTO(ProvisioningLogs provisioningLogs) {
            this
                    (
                            provisioningLogs.getId(),
                            provisioningLogs.getTenantId() != null ? provisioningLogs.getTenantId().getId() : null,
                            provisioningLogs.getTenantId() != null ? provisioningLogs.getTenantId().getCodigo() : null,
                            provisioningLogs.getTenantId() != null ? provisioningLogs.getTenantId().getNome() : null,
                            provisioningLogs.getEtapa(),
                            provisioningLogs.getStatus(),
                            provisioningLogs.getMensagem(),
                            provisioningLogs.getDetalhes(),
                            provisioningLogs.getExecutadoPor() != null ? provisioningLogs.getExecutadoPor().getId() : null,
                            provisioningLogs.getExecutadoPor() != null ? provisioningLogs.getExecutadoPor().getLogin() : null,
                            provisioningLogs.getExecutadoPor() != null ? provisioningLogs.getExecutadoPor().getNome() : null,
                            provisioningLogs.getCreatedAt()
                    );
        }
}
