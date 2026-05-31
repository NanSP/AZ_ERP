package com.example.backend.sys.perfilPermissao;

import com.example.backend.shared.exception.GlobalExceptionHandler;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.permissoes.Permissoes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PerfilPermissaoControllerIntegrationTest {

    @Mock
    private PerfilPermissaoRepository repository;
    @Mock
    private PerfilPermissaoService perfilPermissaoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PerfilPermissaoController(repository, perfilPermissaoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarVinculoPerfilPermissao() throws Exception {
        PerfilPermissaoRequestDTO request = new PerfilPermissaoRequestDTO(2, 3);
        PerfilPermissao entity = new PerfilPermissao();
        entity.setId(20);
        entity.setPerfil(criarPerfil(2));
        entity.setPermissao(criarPermissao(3));

        when(perfilPermissaoService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/sys/perfilPermissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.perfil").value(2))
                .andExpect(jsonPath("$.permissao").value(3));
    }

    @Test
    void deveTraduzirErroDeValidacaoNoVinculoPerfilPermissao() throws Exception {
        PerfilPermissaoRequestDTO request = new PerfilPermissaoRequestDTO(2, 3);

        when(perfilPermissaoService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe vinculo entre o perfil e a permissao informados"));

        mockMvc.perform(post("/sys/perfilPermissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe vinculo entre o perfil e a permissao informados"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private Perfis criarPerfil(Integer id) {
        Perfis perfil = new Perfis();
        perfil.setId(id);
        return perfil;
    }

    private Permissoes criarPermissao(Integer id) {
        Permissoes permissao = new Permissoes();
        permissao.setId(id);
        return permissao;
    }
}
