package com.example.backend.shared.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresJdbcUrlBuilderTest {

    @Test
    void deveManterUrlSemSslParaHostLocal() {
        assertEquals(
                "jdbc:postgresql://localhost:5432/az_erp",
                PostgresJdbcUrlBuilder.build("localhost", 5432, "az_erp")
        );
    }

    @Test
    void deveManterUrlSemSslParaHostInternoDeContainer() {
        assertEquals(
                "jdbc:postgresql://az_erp_postgres:5432/az_erp",
                PostgresJdbcUrlBuilder.build("az_erp_postgres", 5432, "az_erp")
        );
    }

    @Test
    void deveExigirSslParaHostRemoto() {
        assertEquals(
                "jdbc:postgresql://dpg-d8pgtg0js32c738ofbd0-a.ohio-postgres.render.com:5432/az_erp?sslmode=require",
                PostgresJdbcUrlBuilder.build(
                        "dpg-d8pgtg0js32c738ofbd0-a.ohio-postgres.render.com",
                        5432,
                        "az_erp"
                )
        );
    }
}
