package com.example.backend.tenant.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceRoutingConfig {

    @Bean
    public DataSource dataSource(
            DataSource masterDataSource,
            TenantDataSourceRegistry tenantDataSourceRegistry
    ) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("MASTER", masterDataSource);

        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);

        return routingDataSource;
    }
}
