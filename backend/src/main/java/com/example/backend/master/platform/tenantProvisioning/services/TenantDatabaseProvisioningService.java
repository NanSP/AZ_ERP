package com.example.backend.master.platform.tenantProvisioning.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TenantDatabaseProvisioningService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.datasource.template.database}")
    private String templateDatabase;

    public TenantDatabaseProvisioningService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTenantDatabase(String databaseName) {
        validarNomeBanco(databaseName);

        String sql = String.format(
                "CREATE DATABASE %s TEMPLATE %s",
                databaseName,
                templateDatabase
        );

        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            throw new TenantDatabaseProvisioningException(
                    "Erro ao criar banco do tenant: " + databaseName,
                    ex
            );
        }
    }

    private void validarNomeBanco(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("Nome do banco é obrigatório");
        }

        if (!databaseName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Nome do banco contém caracteres inválidos");
        }
    }
}
