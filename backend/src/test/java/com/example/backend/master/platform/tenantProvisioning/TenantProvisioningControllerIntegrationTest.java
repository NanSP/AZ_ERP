package com.example.backend.master.platform.tenantProvisioning;

import com.example.backend.master.platform.tenantProvisioning.services.TenantProvisioningOrchestratorService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TenantProvisioningControllerIntegrationTest {

    @Mock
    private TenantProvisioningOrchestratorService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TenantProvisioningController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveProvisionarTenantEResponderCreated() throws Exception {
        TenantProvisioningRequestDTO request = new TenantProvisioningRequestDTO(
                1L,
                "TENANT_A",
                "Tenant A",
                "Tenant A LTDA",
                "12345678000199",
                "CNPJ",
                "contato@tenant.com",
                "71999999999",
                "PROFESSIONAL",
                "tenant_a_db",
                "localhost",
                5432,
                "tenant_user",
                "tenant_pass",
                "Admin Tenant",
                "admin@tenant.com",
                "admin.tenant",
                "Senha123"
        );
        TenantProvisioningResponseDTO response = new TenantProvisioningResponseDTO(
                10L,
                "TENANT_A",
                "Tenant A",
                "ATIVO",
                20L,
                "tenant_a_db",
                "ATIVO",
                30L,
                "Admin Tenant",
                "admin@tenant.com",
                "admin.tenant",
                LocalDateTime.of(2026, 6, 1, 12, 0),
                List.of("TENANT_CREATED", "DATABASE_REGISTERED", "DATABASE_PROVISIONED")
        );

        when(service.provision(request)).thenReturn(response);

        mockMvc.perform(post("/platform/tenantProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantId").value(10))
                .andExpect(jsonPath("$.tenantCodigo").value("TENANT_A"))
                .andExpect(jsonPath("$.provisionStatus").value("ATIVO"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNoProvisionamento() throws Exception {
        TenantProvisioningRequestDTO request = new TenantProvisioningRequestDTO(
                1L,
                "TENANT_A",
                "Tenant A",
                "Tenant A LTDA",
                "12345678000199",
                "CNPJ",
                "contato@tenant.com",
                "71999999999",
                "PROFESSIONAL",
                "tenant_a_db",
                "localhost",
                5432,
                "tenant_user",
                "tenant_pass",
                "Admin Tenant",
                "admin@tenant.com",
                "admin.tenant",
                "Senha123"
        );

        when(service.provision(request)).thenThrow(new ValidacaoException("Ja existe tenant com o codigo informado"));

        mockMvc.perform(post("/platform/tenantProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe tenant com o codigo informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
