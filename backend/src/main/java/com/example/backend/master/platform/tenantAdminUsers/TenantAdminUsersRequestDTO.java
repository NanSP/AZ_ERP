package com.example.backend.master.platform.tenantAdminUsers;

public record TenantAdminUsersRequestDTO
        (
                Long tenantId,
                String nome,
                String email,
                String login,
                String senha,
                String role,
                String status
        ) {
}
