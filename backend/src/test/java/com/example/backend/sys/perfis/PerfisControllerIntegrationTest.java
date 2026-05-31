package com.example.backend.sys.perfis;

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
class PerfisControllerIntegrationTest {

    @Mock
    private PerfisRepository repository;
    @Mock
    private PerfisService perfisService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PerfisController(repository, perfisService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarPerfil() throws Exception {
        PerfisRequestDTO request = new PerfisRequestDTO(
                "OPERADOR",
                "Perfil para operacao do sistema",
                3
        );
        Perfis entity = new Perfis();
        entity.setId(50);
        entity.setNome("OPERADOR");
        entity.setDescricao("Perfil para operacao do sistema");
        entity.setNivelAcesso(3);
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 11, 0));

        when(perfisService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/sys/perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.nome").value("OPERADOR"))
                .andExpect(jsonPath("$.descricao").value("Perfil para operacao do sistema"))
                .andExpect(jsonPath("$.nivelAcesso").value(3));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoPerfil() throws Exception {
        PerfisRequestDTO request = new PerfisRequestDTO(
                "OPERADOR",
                "Perfil para operacao do sistema",
                3
        );

        when(perfisService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe perfil com o nome informado"));

        mockMvc.perform(post("/sys/perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe perfil com o nome informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
