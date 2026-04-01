package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.tenants.Tenants;
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
                SystemUsers executadoPor,
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
                            provisioningLogs.getExecutadoPor(),
                            provisioningLogs.getCreatedAt()
                    );
        }
}
