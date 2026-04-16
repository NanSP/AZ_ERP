package com.example.backend.tenant.context;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MasterDataSourceConfig {

    private final MasterConnectionProperties masterConnectionProperties;

    public MasterDataSourceConfig(MasterConnectionProperties masterConnectionProperties) {
        this.masterConnectionProperties = masterConnectionProperties;
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(masterConnectionProperties.jdbcUrl());
        dataSource.setUsername(masterConnectionProperties.getUsername());
        dataSource.setPassword(masterConnectionProperties.getPassword());
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setPoolName("master-datasource");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(1);
        return dataSource;
    }

    @Bean(name = "masterJdbcTemplate")
    public JdbcTemplate masterJdbcTemplate(
            @Qualifier("masterDataSource") DataSource masterDataSource
    ) {
        return new JdbcTemplate(masterDataSource);
    }
}
