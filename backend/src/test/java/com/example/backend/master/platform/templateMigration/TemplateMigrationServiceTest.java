package com.example.backend.master.platform.templateMigration;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRequestDTO;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsService;
import com.example.backend.shared.exception.ValidacaoException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TemplateMigrationServiceTest {

    @Mock
    private ProvisioningLogsService provisioningLogsService;

    @Mock
    private TemplateMigrationProperties properties;

    @InjectMocks
    private TemplateMigrationService service;

    @Test
    void deveMigrarTemplateERegistrarLogsDeInicioEFim() {
        FluentConfiguration configuration = mock(FluentConfiguration.class);
        Flyway flyway = mock(Flyway.class);

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(properties.getHost()).thenReturn("localhost");
        when(properties.getPort()).thenReturn(5432);
        when(properties.getUsername()).thenReturn("postgres");
        when(properties.getPassword()).thenReturn("secret");
        when(properties.buildJdbcUrl()).thenReturn("jdbc:postgresql://localhost:5432/az_erp_template");
        when(configuration.dataSource("jdbc:postgresql://localhost:5432/az_erp_template", "postgres", "secret"))
                .thenReturn(configuration);
        when(configuration.locations("classpath:db/migration/template")).thenReturn(configuration);
        when(configuration.baselineOnMigrate(true)).thenReturn(configuration);
        when(configuration.load()).thenReturn(flyway);

        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            flywayStatic.when(Flyway::configure).thenReturn(configuration);

            service.migrateTemplate(99L);
        }

        verify(flyway).migrate();
        verify(provisioningLogsService, times(2)).criar(any(ProvisioningLogsRequestDTO.class));
    }

    @Test
    void deveRetornarInfoDoTemplateERegistrarLog() {
        FluentConfiguration configuration = mock(FluentConfiguration.class);
        Flyway flyway = mock(Flyway.class);
        MigrationInfoService infoService = mock(MigrationInfoService.class);
        MigrationInfo migrationInfo = mock(MigrationInfo.class);

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(properties.getUsername()).thenReturn("postgres");
        when(properties.getPassword()).thenReturn("secret");
        when(properties.buildJdbcUrl()).thenReturn("jdbc:postgresql://localhost:5432/az_erp_template");
        when(configuration.dataSource("jdbc:postgresql://localhost:5432/az_erp_template", "postgres", "secret"))
                .thenReturn(configuration);
        when(configuration.locations("classpath:db/migration/template")).thenReturn(configuration);
        when(configuration.baselineOnMigrate(true)).thenReturn(configuration);
        when(configuration.load()).thenReturn(flyway);
        when(flyway.info()).thenReturn(infoService);
        when(infoService.current()).thenReturn(migrationInfo);
        when(migrationInfo.getVersion()).thenReturn(org.flywaydb.core.api.MigrationVersion.fromVersion("14"));
        when(migrationInfo.getDescription()).thenReturn("bi create tables");

        String info;
        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            flywayStatic.when(Flyway::configure).thenReturn(configuration);
            info = service.infoTemplate(99L);
        }

        assertEquals("Versao atual do template: 14 - bi create tables", info);

        ArgumentCaptor<ProvisioningLogsRequestDTO> captor = ArgumentCaptor.forClass(ProvisioningLogsRequestDTO.class);
        verify(provisioningLogsService).criar(captor.capture());
        assertEquals("TEMPLATE_INFO", captor.getValue().etapa());
        assertEquals("INFO", captor.getValue().status());
    }

    @Test
    void deveConverterFalhaDeMigracaoEmValidacaoException() {
        FluentConfiguration configuration = mock(FluentConfiguration.class);

        when(properties.getDatabase()).thenReturn("az_erp_template");
        when(properties.getHost()).thenReturn("localhost");
        when(properties.getPort()).thenReturn(5432);
        when(properties.getUsername()).thenReturn("postgres");
        when(properties.getPassword()).thenReturn("secret");
        when(properties.buildJdbcUrl()).thenReturn("jdbc:postgresql://localhost:5432/az_erp_template");
        when(configuration.dataSource("jdbc:postgresql://localhost:5432/az_erp_template", "postgres", "secret"))
                .thenReturn(configuration);
        when(configuration.locations("classpath:db/migration/template")).thenReturn(configuration);
        when(configuration.baselineOnMigrate(true)).thenReturn(configuration);
        when(configuration.load()).thenThrow(new RuntimeException("flyway down"));

        ValidacaoException exception;
        try (MockedStatic<Flyway> flywayStatic = mockStatic(Flyway.class)) {
            flywayStatic.when(Flyway::configure).thenReturn(configuration);
            exception = assertThrows(ValidacaoException.class, () -> service.migrateTemplate(99L));
        }

        assertEquals("Erro ao migrar template: flyway down", exception.getMessage());
        verify(provisioningLogsService, times(2)).criar(any(ProvisioningLogsRequestDTO.class));
    }
}
