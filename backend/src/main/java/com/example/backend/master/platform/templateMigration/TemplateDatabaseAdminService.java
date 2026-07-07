package com.example.backend.master.platform.templateMigration;

import com.example.backend.shared.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TemplateDatabaseAdminService {

    private final JdbcTemplate masterJdbcTemplate;

    public TemplateDatabaseAdminService(@Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
    }

    public boolean databaseExists(String databaseName) {
        Boolean exists = masterJdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM pg_database WHERE datname = ?)",
                Boolean.class,
                databaseName
        );

        return Boolean.TRUE.equals(exists);
    }

    public void createDatabase(String databaseName) {
        validarNomeBanco(databaseName);
        masterJdbcTemplate.execute("CREATE DATABASE " + quoteIdentifier(databaseName));
    }

    public void terminateConnections(String databaseName) {
        validarNomeBanco(databaseName);

        Integer activeConnections = masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                Integer.class,
                databaseName
        );

        if (activeConnections == null || activeConnections == 0) {
            return;
        }

        try {
            masterJdbcTemplate.query(
                    "SELECT pg_catalog.pg_terminate_backend(pid) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                    rs -> {
                    },
                    databaseName
            );
        } catch (DataAccessException ex) {
            throw new ValidacaoException(
                    "Nao foi possivel encerrar conexoes ativas do banco template. Verifique os privilegios do PostgreSQL gerenciado."
            );
        }
    }

    public void setConnectionsAllowed(String databaseName, boolean allowed) {
        validarNomeBanco(databaseName);
        masterJdbcTemplate.execute(
                "ALTER DATABASE " + quoteIdentifier(databaseName) + " WITH ALLOW_CONNECTIONS " + allowed
        );
    }

    private void validarNomeBanco(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new ValidacaoException("Nome do banco template e obrigatorio");
        }

        if (!databaseName.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidacaoException("Nome do banco template contem caracteres invalidos");
        }
    }

    public String quoteIdentifier(String databaseName) {
        validarNomeBanco(databaseName);
        return "\"" + databaseName + "\"";
    }
}
