package com.example.backend.master.platform.templateMigration;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.BadSqlGrammarException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateDatabaseAdminServiceTest {

    @Mock
    private JdbcTemplate masterJdbcTemplate;

    @InjectMocks
    private TemplateDatabaseAdminService service;

    @Test
    void deveConsultarExistenciaDoBancoTemplate() {
        when(masterJdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM pg_database WHERE datname = ?)",
                Boolean.class,
                "az_erp_template"
        )).thenReturn(true);

        boolean exists = service.databaseExists("az_erp_template");

        assertEquals(true, exists);
    }

    @Test
    void deveCriarBancoTemplateQuandoNomeForValido() {
        service.createDatabase("az_erp_template");

        verify(masterJdbcTemplate).execute("CREATE DATABASE \"az_erp_template\"");
    }

    @Test
    void deveAlternarPermissaoDeConexaoDoTemplate() {
        service.setConnectionsAllowed("az_erp_template", false);

        verify(masterJdbcTemplate).execute("ALTER DATABASE \"az_erp_template\" WITH ALLOW_CONNECTIONS false");
    }

    @Test
    void deveEncerrarConexoesDoTemplate() {
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                Integer.class,
                "az_erp_template"
        )).thenReturn(2);

        service.terminateConnections("az_erp_template");

        verify(masterJdbcTemplate).query(
                eq("SELECT pg_catalog.pg_terminate_backend(pid) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()"),
                any(RowCallbackHandler.class),
                eq("az_erp_template")
        );
    }

    @Test
    void deveIgnorarEncerramentoQuandoNaoHaConexoesAtivas() {
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                Integer.class,
                "az_erp_template"
        )).thenReturn(0);

        service.terminateConnections("az_erp_template");
    }

    @Test
    void deveTraduzirFalhaDePermissaoAoEncerrarConexoes() {
        when(masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                Integer.class,
                "az_erp_template"
        )).thenReturn(1);

        BadSqlGrammarException failure = new BadSqlGrammarException(
                "terminate",
                "SELECT pg_catalog.pg_terminate_backend(pid) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()",
                null
        );

        org.mockito.Mockito.doThrow(failure)
                .when(masterJdbcTemplate)
                .query(
                        eq("SELECT pg_catalog.pg_terminate_backend(pid) FROM pg_catalog.pg_stat_activity WHERE datname = ? AND pid <> pg_backend_pid()"),
                        any(RowCallbackHandler.class),
                        eq("az_erp_template")
                );

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> service.terminateConnections("az_erp_template")
        );

        assertEquals(
                "Nao foi possivel encerrar conexoes ativas do banco template. Verifique os privilegios do PostgreSQL gerenciado.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearCriacaoComNomeInvalido() {
        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> service.createDatabase("az-erp-template")
        );

        assertEquals("Nome do banco template contem caracteres invalidos", exception.getMessage());
    }
}
