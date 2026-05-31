package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.shared.exception.GlobalExceptionHandler;
import com.example.backend.shared.exception.ValidacaoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TenantDatabasesControllerIntegrationTest {

    @Mock
    private TenantDatabasesRepository repository;
    @Mock
    private TenantDatabasesService tenantDatabasesService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TenantDatabasesController(repository, tenantDatabasesService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarTenantDatabase() throws Exception {
        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                "acme_db",
                "template_v1",
                "localhost",
                5432,
                "postgres",
                "segredo",
                "PENDENTE",
                LocalDateTime.of(2026, 6, 1, 12, 30)
        );
        TenantDatabases entity = new TenantDatabases();
        entity.setId(70L);
        entity.setTenantId(criarTenant(1L, "acme", "Acme Ltda"));
        entity.setDatabaseName("acme_db");
        entity.setTemplateName("template_v1");
        entity.setDbHost("localhost");
        entity.setDbPort(5432);
        entity.setDbUsername("postgres");
        entity.setProvisionStatus("PENDENTE");
        entity.setLastCheckAt(LocalDateTime.of(2026, 6, 1, 12, 30));
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));

        when(tenantDatabasesService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/platform/tenantDatabases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 1,
                                  "databaseName": "acme_db",
                                  "templateName": "template_v1",
                                  "dbHost": "localhost",
                                  "dbPort": 5432,
                                  "dbUsername": "postgres",
                                  "dbPassword": "segredo",
                                  "provisionStatus": "PENDENTE",
                                  "lastCheckAt": "2026-06-01T12:30:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(70))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.tenantCodigo").value("acme"))
                .andExpect(jsonPath("$.databaseName").value("acme_db"))
                .andExpect(jsonPath("$.provisionStatus").value("PENDENTE"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoTenantDatabase() throws Exception {
        TenantDatabasesRequestDTO request = new TenantDatabasesRequestDTO(
                1L,
                "acme_db",
                "template_v1",
                "localhost",
                5432,
                "postgres",
                "segredo",
                "PENDENTE",
                LocalDateTime.of(2026, 6, 1, 12, 30)
        );

        when(tenantDatabasesService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe tenant database com o database name informado"));

        mockMvc.perform(post("/platform/tenantDatabases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 1,
                                  "databaseName": "acme_db",
                                  "templateName": "template_v1",
                                  "dbHost": "localhost",
                                  "dbPort": 5432,
                                  "dbUsername": "postgres",
                                  "dbPassword": "segredo",
                                  "provisionStatus": "PENDENTE",
                                  "lastCheckAt": "2026-06-01T12:30:00"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe tenant database com o database name informado"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveBuscarTenantDatabasePorId() throws Exception {
        TenantDatabases entity = new TenantDatabases();
        entity.setId(70L);
        entity.setTenantId(criarTenant(1L, "acme", "Acme Ltda"));
        entity.setDatabaseName("acme_db");
        entity.setTemplateName("template_v1");
        entity.setDbHost("localhost");
        entity.setDbPort(5432);
        entity.setDbUsername("postgres");
        entity.setProvisionStatus("ATIVO");
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 12, 5));

        when(repository.findById(70L)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/platform/tenantDatabases/70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(70))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.tenantCodigo").value("acme"))
                .andExpect(jsonPath("$.databaseName").value("acme_db"));
    }

    @Test
    void deveTraduzirNaoEncontradoAoBuscarTenantDatabasePorId() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/platform/tenantDatabases/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tenant database nao encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveExcluirTenantDatabase() throws Exception {
        mockMvc.perform(delete("/platform/tenantDatabases/70"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tenant database deleted"));

        verify(tenantDatabasesService).excluir(70L);
    }

    private Tenants criarTenant(Long id, String codigo, String nome) {
        Tenants tenant = new Tenants();
        tenant.setId(id);
        tenant.setCodigo(codigo);
        tenant.setNome(nome);
        return tenant;
    }
}
