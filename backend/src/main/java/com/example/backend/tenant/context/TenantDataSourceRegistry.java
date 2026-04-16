package com.example.backend.tenant.context;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantDataSourceRegistry {

    private final MasterConnectionProperties masterConnectionProperties;
    private final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    public TenantDataSourceRegistry(MasterConnectionProperties masterConnectionProperties) {
        this.masterConnectionProperties = masterConnectionProperties;
    }

    public DataSource getOrCreate(String tenantCode) {
        return cache.computeIfAbsent(tenantCode, this::createDataSource);
    }

    private DataSource createDataSource(String tenantCode) {
        String sql = """
                SELECT
                    t.id AS tenant_id,
                    t.codigo AS tenant_code,
                    t.status AS tenant_status,
                    td.database_name,
                    td.db_host,
                    td.db_port,
                    td.db_username,
                    td.db_password_encrypted,
                    td.provision_status
                FROM platform.tenants t
                JOIN platform.tenant_databases td ON td.tenant_id = t.id
                WHERE UPPER(t.codigo) = UPPER(?)
                """;

        try (
                Connection connection = java.sql.DriverManager.getConnection(
                        masterConnectionProperties.jdbcUrl(),
                        masterConnectionProperties.getUsername(),
                        masterConnectionProperties.getPassword()
                );
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, tenantCode);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Tenant nao encontrado");
                }

                String tenantStatus = rs.getString("tenant_status");
                String provisionStatus = rs.getString("provision_status");

                if (!"ATIVO".equalsIgnoreCase(tenantStatus)) {
                    throw new RuntimeException("Tenant inativo");
                }

                if (!"ATIVO".equalsIgnoreCase(provisionStatus)) {
                    throw new RuntimeException("Banco do tenant nao esta ativo");
                }

                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(
                        "jdbc:postgresql://" +
                                rs.getString("db_host") + ":" +
                                rs.getInt("db_port") + "/" +
                                rs.getString("database_name")
                );
                dataSource.setUsername(rs.getString("db_username"));
                dataSource.setPassword(rs.getString("db_password_encrypted"));
                dataSource.setDriverClassName("org.postgresql.Driver");

                dataSource.setPoolName("tenant-" + tenantCode);
                dataSource.setMaximumPoolSize(5);
                dataSource.setMinimumIdle(1);

                return dataSource;
            }

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao criar datasource do tenant: " + ex.getMessage(), ex);
        }
    }

    public void evict(String tenantCode) {
        DataSource dataSource = cache.remove(tenantCode);

        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }

    public void clear() {
        cache.values().forEach(dataSource -> {
            if (dataSource instanceof HikariDataSource hikari) {
                hikari.close();
            }
        });
        cache.clear();
    }
}
