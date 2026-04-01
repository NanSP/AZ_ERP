package com.example.backend.master.platform.tenantAdminUsers;

import java.time.LocalDateTime;

public record TenantAdminUsersRequestDTO
        (
                Long tenantId,
                String nome,
                String email,
                String login,
                String senha,
                String role,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        ) {
}
