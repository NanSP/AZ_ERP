package com.example.backend.tenant.auth;

import java.util.List;

public record TenantAuthResponseDTO(
        String token,
        Long tenantId,
        String tenantCode,
        Long userId,
        String login,
        String role,
        String scope,
        boolean passwordChangeRequired,
        List<String> perfis,
        List<String> permissoes
) {
}

