package com.example.backend.tenant.auth;

public record TenantAuthResponseDTO(
        String token,
        Long tenantId,
        String tenantCode,
        Long userId,
        String login,
        String role,
        String scope
) {
}

