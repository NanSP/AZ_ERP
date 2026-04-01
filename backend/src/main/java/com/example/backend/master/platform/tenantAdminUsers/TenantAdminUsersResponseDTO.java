package com.example.backend.master.platform.tenantAdminUsers;

import java.time.LocalDateTime;

public record TenantAdminUsersResponseDTO
        (
                Long id,
                Long tenantId,
                String tenantCodigo,
                String tenantNome,
                String nome,
                String email,
                String login,
                String role,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        )
    {
        public TenantAdminUsersResponseDTO(TenantAdminUsers tenantAdminUsers) {
            this
                    (
                            tenantAdminUsers.getId(),
                            tenantAdminUsers.getTenantId() != null ? tenantAdminUsers.getTenantId().getId() : null,
                            tenantAdminUsers.getTenantId() != null ? tenantAdminUsers.getTenantId().getCodigo() : null,
                            tenantAdminUsers.getTenantId() != null ? tenantAdminUsers.getTenantId().getNome() : null,
                            tenantAdminUsers.getNome(),
                            tenantAdminUsers.getEmail(),
                            tenantAdminUsers.getLogin(),
                            tenantAdminUsers.getRole(),
                            tenantAdminUsers.getStatus(),
                            tenantAdminUsers.getUltimoAcesso(),
                            tenantAdminUsers.getCreatedAt(),
                            tenantAdminUsers.getUpdatedAt()
                    );
        }
}
