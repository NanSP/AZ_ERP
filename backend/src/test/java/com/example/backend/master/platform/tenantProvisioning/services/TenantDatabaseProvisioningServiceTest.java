package com.example.backend.master.platform.tenantProvisioning.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TenantDatabaseProvisioningServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TenantDatabaseProvisioningService service;

    @Test
    void deveExecutarSqlDeCriacaoDoBanco() {
        ReflectionTestUtils.setField(service, "templateDatabase", "az_erp_template");

        service.createTenantDatabase("tenant_db_01");

        verify(jdbcTemplate).execute("CREATE DATABASE tenant_db_01 TEMPLATE az_erp_template");
    }

    @Test
    void deveBloquearNomeDeBancoComCaracteresInvalidos() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createTenantDatabase("tenant-db-01")
        );

        assertEquals("Nome do banco contém caracteres inválidos", exception.getMessage());
    }

    @Test
    void deveEncapsularFalhaTecnicaNaCriacaoDoBanco() {
        ReflectionTestUtils.setField(service, "templateDatabase", "az_erp_template");
        doThrow(new RuntimeException("erro postgres"))
                .when(jdbcTemplate)
                .execute("CREATE DATABASE tenant_db_01 TEMPLATE az_erp_template");

        TenantDatabaseProvisioningException exception = assertThrows(
                TenantDatabaseProvisioningException.class,
                () -> service.createTenantDatabase("tenant_db_01")
        );

        assertEquals("Erro ao criar banco do tenant: tenant_db_01", exception.getMessage());
    }
}
