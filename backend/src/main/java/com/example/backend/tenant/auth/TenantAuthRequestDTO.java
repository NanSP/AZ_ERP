package com.example.backend.tenant.auth;

public record TenantAuthRequestDTO(
        String tenantCode,
        String login,
        String senha
) {
}

