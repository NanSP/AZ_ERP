package com.example.backend.tenant.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record TenantAuthResponseDTO(
        @JsonIgnore
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

