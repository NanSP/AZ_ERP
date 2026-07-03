package com.example.backend.tenant.auth;

public record TenantForgotPasswordRequestDTO(
        String tenantCode,
        String identificador
) {
}
