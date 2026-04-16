package com.example.backend.tenant.context;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantCode = TenantContext.getTenant();

        if (tenantCode == null || tenantCode.isBlank()) {
            return "MASTER";
        }

        return tenantCode;
    }
}
