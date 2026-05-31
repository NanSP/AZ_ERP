package com.example.backend.sys.usuarios;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuariosControllerIntegrationTest {

    @Mock
    private UsuariosRepository repository;
    @Mock
    private UsuariosService usuariosService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuariosController(repository, usuariosService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarUsuario() throws Exception {
        UsuariosRequestDTO request = new UsuariosRequestDTO(
                "Ana Silva",
                "ana@empresa.com",
                "ana.silva",
                "Senha@123",
                "12345678901",
                "COLABORADOR",
                "ATIVO",
                null,
                LocalDate.of(2026, 7, 1),
                0
        );
        Usuarios entity = new Usuarios();
        entity.setId(40);
        entity.setNome("Ana Silva");
        entity.setEmail("ana@empresa.com");
        entity.setLogin("ana.silva");
        entity.setDocumento("12345678901");
        entity.setTipoUsuario("COLABORADOR");
        entity.setStatus("ATIVO");
        entity.setUltimoAcesso(LocalDateTime.of(2026, 6, 1, 8, 30));
        entity.setExpiracaoSenha(LocalDate.of(2026, 7, 1));
        entity.setTentativasLogin(0);
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 8, 0));

        when(usuariosService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/sys/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Silva",
                                  "email": "ana@empresa.com",
                                  "login": "ana.silva",
                                  "senha": "Senha@123",
                                  "documento": "12345678901",
                                  "tipoUsuario": "COLABORADOR",
                                  "status": "ATIVO",
                                  "ultimoAcesso": null,
                                  "expiracaoSenha": "2026-07-01",
                                  "tentativasLogin": 0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(40))
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana@empresa.com"))
                .andExpect(jsonPath("$.login").value("ana.silva"))
                .andExpect(jsonPath("$.tipoUsuario").value("COLABORADOR"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoUsuario() throws Exception {
        UsuariosRequestDTO request = new UsuariosRequestDTO(
                "Ana Silva",
                "ana@empresa.com",
                "ana.silva",
                "Senha@123",
                "12345678901",
                "COLABORADOR",
                "ATIVO",
                null,
                LocalDate.of(2026, 7, 1),
                0
        );

        when(usuariosService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe usuario com o email informado"));

        mockMvc.perform(post("/sys/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Silva",
                                  "email": "ana@empresa.com",
                                  "login": "ana.silva",
                                  "senha": "Senha@123",
                                  "documento": "12345678901",
                                  "tipoUsuario": "COLABORADOR",
                                  "status": "ATIVO",
                                  "ultimoAcesso": null,
                                  "expiracaoSenha": "2026-07-01",
                                  "tentativasLogin": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe usuario com o email informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
