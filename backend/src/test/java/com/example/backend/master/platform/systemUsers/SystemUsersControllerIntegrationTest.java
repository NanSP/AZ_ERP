package com.example.backend.master.platform.systemUsers;

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
class SystemUsersControllerIntegrationTest {

    @Mock
    private SystemUsersRepository repository;
    @Mock
    private SystemUsersService systemUsersService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SystemUsersController(repository, systemUsersService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarSystemUser() throws Exception {
        SystemUsersRequestDTO request = new SystemUsersRequestDTO(
                "Operador Master",
                "master@empresa.com",
                "master.ops",
                "Senha@123",
                "OPERATIONS",
                "ATIVO"
        );
        SystemUsers entity = new SystemUsers();
        entity.setId(90L);
        entity.setNome("Operador Master");
        entity.setEmail("master@empresa.com");
        entity.setLogin("master.ops");
        entity.setRole("OPERATIONS");
        entity.setStatus("ATIVO");
        entity.setUltimoAcesso(LocalDateTime.of(2026, 6, 1, 13, 15));
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 13, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 13, 0));

        when(systemUsersService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/platform/systemUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(90))
                .andExpect(jsonPath("$.nome").value("Operador Master"))
                .andExpect(jsonPath("$.email").value("master@empresa.com"))
                .andExpect(jsonPath("$.login").value("master.ops"))
                .andExpect(jsonPath("$.role").value("OPERATIONS"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoSystemUser() throws Exception {
        SystemUsersRequestDTO request = new SystemUsersRequestDTO(
                "Operador Master",
                "master@empresa.com",
                "master.ops",
                "Senha@123",
                "OPERATIONS",
                "ATIVO"
        );

        when(systemUsersService.criar(request))
                .thenThrow(new ValidacaoException("Ja existe system user com o login informado"));

        mockMvc.perform(post("/platform/systemUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe system user com o login informado"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
