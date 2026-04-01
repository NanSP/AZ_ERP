package com.example.backend.master.platform.tenantProvisioning;

public record TenantProvisioningRequestDTO(
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
        String dbPasswordEncrypted,
        String adminNome,
        String adminEmail,
        String adminLogin,
        String adminSenhaHash
) {}

