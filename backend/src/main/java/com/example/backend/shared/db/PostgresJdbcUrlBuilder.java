package com.example.backend.shared.db;

import java.util.Locale;

public final class PostgresJdbcUrlBuilder {

    private PostgresJdbcUrlBuilder() {
    }

    public static String build(String host, Integer port, String database) {
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        return shouldRequireSsl(host) ? jdbcUrl + "?sslmode=require" : jdbcUrl;
    }

    static boolean shouldRequireSsl(String host) {
        if (host == null || host.isBlank()) {
            return false;
        }

        String normalized = host.trim().toLowerCase(Locale.ROOT);

        if (normalized.equals("localhost")
                || normalized.equals("127.0.0.1")
                || normalized.equals("::1")
                || normalized.equals("host.docker.internal")) {
            return false;
        }

        if (!normalized.contains(".")) {
            return false;
        }

        return true;
    }
}
