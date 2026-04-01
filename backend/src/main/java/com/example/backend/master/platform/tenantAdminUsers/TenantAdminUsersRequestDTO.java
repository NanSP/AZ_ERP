package com.example.backend.master.platform.tenantAdminUsers;

import java.time.LocalDateTime;

public record TenantAdminUsersRequestDTO
        (
                Long tenantId,
                String tenantCodigo,
                String tenantNome,
                String nome,
                String email,
                String login,
                String senhaHash,
                String role,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        ) {
}
