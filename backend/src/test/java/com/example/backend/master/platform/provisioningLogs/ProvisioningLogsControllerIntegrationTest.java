package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
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
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProvisioningLogsControllerIntegrationTest {

    @Mock
    private ProvisioningLogsRepository repository;
    @Mock
    private ProvisioningLogsService provisioningLogsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProvisioningLogsController(repository, provisioningLogsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarProvisioningLog() throws Exception {
        ProvisioningLogsRequestDTO request = new ProvisioningLogsRequestDTO(
                1L,
                "CRIACAO_DATABASE",
                "SUCESSO",
                "Database criada com sucesso",
                Map.of("database", "acme_db"),
                2L
        );
        ProvisioningLogs entity = new ProvisioningLogs();
        entity.setId(100L);
        entity.setTenantId(criarTenant(1L, "acme", "Acme Ltda"));
        entity.setEtapa("CRIACAO_DATABASE");
        entity.setStatus("SUCESSO");
        entity.setMensagem("Database criada com sucesso");
        entity.setDetalhes(Map.of("database", "acme_db"));
        entity.setExecutadoPor(criarSystemUser(2L, "master.ops", "Operador Master"));
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 13, 30));

        when(provisioningLogsService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/platform/provisioningLogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.tenantCodigo").value("acme"))
                .andExpect(jsonPath("$.status").value("SUCESSO"))
                .andExpect(jsonPath("$.executadoPorId").value(2))
                .andExpect(jsonPath("$.executadoPorLogin").value("master.ops"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoProvisioningLog() throws Exception {
        ProvisioningLogsRequestDTO request = new ProvisioningLogsRequestDTO(
                1L,
                "CRIACAO_DATABASE",
                "SUCESSO",
                "Database criada com sucesso",
                Map.of("database", "acme_db"),
                2L
        );

        when(provisioningLogsService.criar(request))
                .thenThrow(new ValidacaoException("Tenant fora do ciclo operacional para provisioning log"));

        mockMvc.perform(post("/platform/provisioningLogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tenant fora do ciclo operacional para provisioning log"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private Tenants criarTenant(Long id, String codigo, String nome) {
        Tenants tenant = new Tenants();
        tenant.setId(id);
        tenant.setCodigo(codigo);
        tenant.setNome(nome);
        return tenant;
    }

    private SystemUsers criarSystemUser(Long id, String login, String nome) {
        SystemUsers systemUser = new SystemUsers();
        systemUser.setId(id);
        systemUser.setLogin(login);
        systemUser.setNome(nome);
        return systemUser;
    }
}
