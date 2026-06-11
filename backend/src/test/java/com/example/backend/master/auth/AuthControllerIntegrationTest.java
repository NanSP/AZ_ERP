package com.example.backend.master.auth;

import com.example.backend.auth.AuthController;
import com.example.backend.security.AuthCookieService;
import com.example.backend.shared.exception.GlobalExceptionHandler;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.tenant.auth.TenantAuthService;
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
class AuthControllerIntegrationTest {

    @Mock
    private MasterAuthService service;

    @Mock
    private TenantAuthService tenantAuthService;

    @Mock
    private AuthCookieService authCookieService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service, tenantAuthService, authCookieService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveResponderOkNoLoginMaster() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("admin", "Senha123");
        AuthResponseDTO response = new AuthResponseDTO("TOKEN", 1L, "admin", "MASTER_ADMIN", "master", true);

        when(service.login(request)).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOKEN"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.scope").value("master"))
                .andExpect(jsonPath("$.passwordChangeRequired").value(true));
    }

    @Test
    void deveTraduzirErroDeValidacaoNoLoginMaster() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("admin", "");

        when(service.login(request)).thenThrow(new ValidacaoException("Senha e obrigatoria"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Senha e obrigatoria"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
