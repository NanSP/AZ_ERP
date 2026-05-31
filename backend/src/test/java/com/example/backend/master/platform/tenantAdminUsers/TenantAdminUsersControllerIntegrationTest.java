package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
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
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TenantAdminUsersControllerIntegrationTest {

    @Mock
    private TenantAdminUsersRepository repository;
    @Mock
    private TenantAdminUsersService tenantAdminUsersService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TenantAdminUsersController(repository, tenantAdminUsersService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void deveCriarTenantAdminUser() throws Exception {
        TenantAdminUsersRequestDTO request = new TenantAdminUsersRequestDTO(
                1L,
                "Admin Acme",
                "admin@acme.com",
                "admin.acme",
                "Senha@123",
                "TENANT_ADMIN",
                "ATIVO"
        );
        TenantAdminUsers entity = new TenantAdminUsers();
        entity.setId(80L);
        entity.setTenantId(criarTenant(1L, "acme", "Acme Ltda"));
        entity.setNome("Admin Acme");
        entity.setEmail("admin@acme.com");
        entity.setLogin("admin.acme");
        entity.setRole("TENANT_ADMIN");
        entity.setStatus("ATIVO");
        entity.setUltimoAcesso(LocalDateTime.of(2026, 6, 1, 13, 0));
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 45));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 12, 45));

        when(tenantAdminUsersService.criar(request)).thenReturn(entity);

        mockMvc.perform(post("/platform/tenantAdminUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(80))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.tenantCodigo").value("acme"))
                .andExpect(jsonPath("$.email").value("admin@acme.com"))
                .andExpect(jsonPath("$.role").value("TENANT_ADMIN"));
    }

    @Test
    void deveTraduzirErroDeValidacaoNaCriacaoTenantAdminUser() throws Exception {
        TenantAdminUsersRequestDTO request = new TenantAdminUsersRequestDTO(
                1L,
                "Admin Acme",
                "admin@acme.com",
                "admin.acme",
                "Senha@123",
                "TENANT_ADMIN",
                "ATIVO"
        );

        when(tenantAdminUsersService.criar(request))
                .thenThrow(new ValidacaoException("Tenant precisa estar ATIVO"));

        mockMvc.perform(post("/platform/tenantAdminUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tenant precisa estar ATIVO"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveBuscarTenantAdminUserPorId() throws Exception {
        TenantAdminUsers entity = new TenantAdminUsers();
        entity.setId(80L);
        entity.setTenantId(criarTenant(1L, "acme", "Acme Ltda"));
        entity.setNome("Admin Acme");
        entity.setEmail("admin@acme.com");
        entity.setLogin("admin.acme");
        entity.setRole("TENANT_ADMIN");
        entity.setStatus("ATIVO");
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 45));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 1, 13, 0));

        when(repository.findById(80L)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/platform/tenantAdminUsers/80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(80))
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.tenantCodigo").value("acme"))
                .andExpect(jsonPath("$.login").value("admin.acme"));
    }

    @Test
    void deveTraduzirNaoEncontradoAoBuscarTenantAdminUserPorId() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/platform/tenantAdminUsers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tenant admin user nao encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveExcluirTenantAdminUser() throws Exception {
        mockMvc.perform(delete("/platform/tenantAdminUsers/80"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tenant admin user deleted"));

        verify(tenantAdminUsersService).excluir(80L);
    }

    private Tenants criarTenant(Long id, String codigo, String nome) {
        Tenants tenant = new Tenants();
        tenant.setId(id);
        tenant.setCodigo(codigo);
        tenant.setNome(nome);
        return tenant;
    }
}
