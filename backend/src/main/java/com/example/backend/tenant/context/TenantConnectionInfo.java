package com.example.backend.tenant.context;

import com.example.backend.shared.db.PostgresJdbcUrlBuilder;

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
        return PostgresJdbcUrlBuilder.build(host, port, databaseName);
    }
}
