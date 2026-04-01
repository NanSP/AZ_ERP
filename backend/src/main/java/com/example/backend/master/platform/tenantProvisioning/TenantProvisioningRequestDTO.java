package com.example.backend.master.platform.tenantProvisioning;

public record TenantProvisioningRequestDTO(
        Long systemUserId,
        String codigo,
        String nome,
        String nomeFantasia,
        String documento,
        String tipoDocumento,
        String emailResponsavel,
        String telefoneResponsavel,
        String plano,
        String databaseName,
        String dbHost,
        Integer dbPort,
        String dbUsername,
        String dbPassword,
        String adminNome,
        String adminEmail,
        String adminLogin,
        String adminSenha
) {}

