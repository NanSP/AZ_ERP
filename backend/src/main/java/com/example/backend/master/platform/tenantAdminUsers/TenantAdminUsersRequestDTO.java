package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;

import java.time.LocalDateTime;

public record TenantAdminUsersRequestDTO
        (
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
        ) {
}
