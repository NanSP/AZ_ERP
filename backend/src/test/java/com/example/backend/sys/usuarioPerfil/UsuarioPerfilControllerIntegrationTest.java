package com.example.backend.sys.usuarioPerfil;

import com.example.backend.shared.exception.GlobalExceptionHandler;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.usuarios.Usuarios;
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
class UsuarioPerfilControllerIntegrationTest {

    @Mock
    private UsuarioPerfilRepository repository;
    @Mock
    private UsuarioPerfilService usuarioPerfilService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioPerfilController(repository, usuarioPerfilService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarVinculoUsuarioPerfil() throws Exception {
        UsuarioPerfilRequestDTO request = new UsuarioPerfilRequestDTO(1, 2);
        UsuarioPerfil entity = new UsuarioPerfil();
        entity.setId(10);
        entity.setUsuario(criarUsuario(1));
        entity.setPerfil(criarPerfil(2));
        entity.setDataAtribuicao(LocalDateTime.of(2026, 6, 1, 10, 0));

        when(usuarioPerfilService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/sys/usuarioPerfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.usuario").value(1))
                .andExpect(jsonPath("$.perfil").value(2));
    }

    @Test
    void deveTraduzirErroDeValidacaoNoVinculoUsuarioPerfil() throws Exception {
        UsuarioPerfilRequestDTO request = new UsuarioPerfilRequestDTO(1, 2);

        when(usuarioPerfilService.criar(request))
                .thenThrow(new ValidacaoException("Usuario informado precisa estar ativo"));

        mockMvc.perform(post("/sys/usuarioPerfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario informado precisa estar ativo"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private Usuarios criarUsuario(Integer id) {
        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        return usuario;
    }

    private Perfis criarPerfil(Integer id) {
        Perfis perfil = new Perfis();
        perfil.setId(id);
        return perfil;
    }
}
