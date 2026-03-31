package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import java.time.LocalDateTime;

public record TenantAdminUsersResponseDTO
        (
                Integer id,
                Tenants tenantId,
                String nome,
                String email,
                String login,
                String senhaHash,
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
                            tenantAdminUsers.getTenantId(),
                            tenantAdminUsers.getNome(),
                            tenantAdminUsers.getEmail(),
                            tenantAdminUsers.getLogin(),
                            tenantAdminUsers.getSenhaHash(),
                            tenantAdminUsers.getRole(),
                            tenantAdminUsers.getStatus(),
                            tenantAdminUsers.getUltimoAcesso(),
                            tenantAdminUsers.getCreatedAt(),
                            tenantAdminUsers.getUpdatedAt()
                    );
        }
}
