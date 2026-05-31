package com.example.backend.sys.permissoes;

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
class PermissoesControllerIntegrationTest {

    @Mock
    private PermissoesRepository repository;
    @Mock
    private PermissoesService permissoesService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PermissoesController(repository, permissoesService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarPermissao() throws Exception {
        PermissoesRequestDTO request = new PermissoesRequestDTO(
                "usuarios.visualizar",
                "Permite visualizar usuarios",
                "SYS",
                "USUARIOS",
                "VISUALIZAR"
        );
        Permissoes entity = new Permissoes();
        entity.setId(30);
        entity.setNome("usuarios.visualizar");
        entity.setDescricao("Permite visualizar usuarios");
        entity.setModulo("SYS");
        entity.setRecurso("USUARIOS");
        entity.setAcao("VISUALIZAR");
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 9, 0));

        when(permissoesService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/sys/permissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.nome").value("usuarios.visualizar"))
                .andExpect(jsonPath("$.modulo").value("SYS"))
                .andExpect(jsonPath("$.recurso").value("USUARIOS"))
                .andExpect(jsonPath("$.acao").value("VISUALIZAR"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoPermissao() throws Exception {
        PermissoesRequestDTO request = new PermissoesRequestDTO(
                "usuarios.visualizar",
                "Permite visualizar usuarios",
                "SYS",
                "USUARIOS",
                "VISUALIZAR"
        );

        when(permissoesService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe permissao com nome informado"));

        mockMvc.perform(post("/sys/permissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe permissao com nome informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
