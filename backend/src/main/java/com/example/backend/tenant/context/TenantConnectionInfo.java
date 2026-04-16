package com.example.backend.tenant.context;

public record TenantConnectionInfo(
        Long tenantId,
        String tenantCode,
        String databaseName,
        String host,
        Integer port,
        String username,
        String password
) {
    public String jdbcUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
    }
}
