package com.example.backend.master.platform.tenants;

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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TenantsControllerIntegrationTest {

    @Mock
    private TenantsRepository repository;
    @Mock
    private TenantsService tenantsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TenantsController(repository, tenantsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarTenant() throws Exception {
        TenantsRequestDTO request = new TenantsRequestDTO(
                "acme",
                "Acme Ltda",
                "Acme",
                "12345678000199",
                "CNPJ",
                "contato@acme.com",
                "71999999999",
                "PENDENTE",
                "STARTER",
                "V1",
                "Tenant inicial"
        );
        Tenants entity = new Tenants();
        entity.setId(60L);
        entity.setCodigo("acme");
        entity.setNome("Acme Ltda");
        entity.setNomeFantasia("Acme");
        entity.setDocumento("12345678000199");
        entity.setTipoDocumento("CNPJ");
        entity.setEmailResponsavel("contato@acme.com");
        entity.setTelefoneResponsavel("71999999999");
        entity.setStatus("PENDENTE");
        entity.setPlano("STARTER");
        entity.setSchemaVersion("V1");
        entity.setObservacoes("Tenant inicial");
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));

        when(tenantsService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/platform/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(60))
                .andExpect(jsonPath("$.codigo").value("acme"))
                .andExpect(jsonPath("$.nome").value("Acme Ltda"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.plano").value("STARTER"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoTenant() throws Exception {
        TenantsRequestDTO request = new TenantsRequestDTO(
                "acme",
                "Acme Ltda",
                "Acme",
                "12345678000199",
                "CNPJ",
                "contato@acme.com",
                "71999999999",
                "PENDENTE",
                "STARTER",
                "V1",
                "Tenant inicial"
        );

        when(tenantsService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe tenant com o codigo informado"));

        mockMvc.perform(post("/platform/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe tenant com o codigo informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
