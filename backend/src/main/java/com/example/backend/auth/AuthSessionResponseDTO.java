package com.example.backend.auth;

import java.util.List;

public record AuthSessionResponseDTO(
        String scope,
        String login,
        Long userId,
        String role,
        Long tenantId,
        String tenantCode,
        List<String> perfis,
        List<String> permissoes,
        boolean passwordChangeRequired
) {
}