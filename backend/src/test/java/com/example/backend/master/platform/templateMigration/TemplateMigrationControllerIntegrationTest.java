package com.example.backend.master.platform.templateMigration;

import com.example.backend.shared.exception.GlobalExceptionHandler;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TemplateMigrationControllerIntegrationTest {

    @Mock
    private TemplateMigrationService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TemplateMigrationController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveExecutarMigracaoDoTemplate() throws Exception {
        mockMvc.perform(post("/platform/templateMigration/migrate")
                        .param("systemUserId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Migrations do template aplicadas com sucesso"));

        verify(service).migrateTemplate(2L);
    }

    @Test
    void deveValidarTemplate() throws Exception {
        mockMvc.perform(get("/platform/templateMigration/validate")
                        .param("systemUserId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template validado com sucesso"));

        verify(service).validateTemplate(2L);
    }

    @Test
    void deveRetornarInfoDoTemplate() throws Exception {
        when(service.infoTemplate(2L)).thenReturn("Template version: V3");

        mockMvc.perform(get("/platform/templateMigration/info")
                        .param("systemUserId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template version: V3"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaMigracaoDoTemplate() throws Exception {
        org.mockito.Mockito.doThrow(new ValidacaoException("Usuario executor precisa estar ATIVO"))
                .when(service)
                .migrateTemplate(2L);

        mockMvc.perform(post("/platform/templateMigration/migrate")
                        .param("systemUserId", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario executor precisa estar ATIVO"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
