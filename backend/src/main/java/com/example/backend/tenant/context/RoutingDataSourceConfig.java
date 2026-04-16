package com.example.backend.tenant.context;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RoutingDataSourceConfig {

    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            TenantDataSourceRegistry tenantDataSourceRegistry
    ) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource() {
            @Override
            protected DataSource determineTargetDataSource() {
                Object lookupKey = determineCurrentLookupKey();

                if (TenantRoutingDataSource.MASTER_KEY.equals(lookupKey)) {
                    return masterDataSource;
                }

                return tenantDataSourceRegistry.getOrCreate(String.valueOf(lookupKey));
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(TenantRoutingDataSource.MASTER_KEY, masterDataSource);

        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}
