package com.example.backend.tenant.auth;

public record TenantForgotPasswordResponseDTO(
        String tenantCode,
        String mensagem,
        String emailResponsavel,
        String telefoneResponsavel
) {
}
