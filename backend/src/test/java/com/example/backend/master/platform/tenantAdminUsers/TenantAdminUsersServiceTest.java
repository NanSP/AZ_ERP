package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantAdminUsersServiceTest {

    @Mock
    private TenantAdminUsersRepository repository;

    @Mock
    private TenantsRepository tenantsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TenantAdminUsersService service;

    @Test
    void deveCriarAdminQuandoTenantEstiverAtivo() {
        TenantAdminUsersRequestDTO request = new TenantAdminUsersRequestDTO(
                1L,
                "Admin Tenant",
                "ADMIN@TENANT.COM",
                " Tenant.Admin ",
                "senhaForte123",
                "TENANT_ADMIN",
                "ATIVO"
        );
        Tenants tenant = criarTenant("ATIVO");

        when(tenantsRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode("senhaForte123")).thenReturn("senha-hash");
        when(repository.save(org.mockito.ArgumentMatchers.any(TenantAdminUsers.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TenantAdminUsers saved = service.criar(request);

        assertEquals("admin@tenant.com", saved.getEmail());
        assertEquals("tenant.admin", saved.getLogin());
        assertEquals("senha-hash", saved.getSenha());
        assertEquals("TENANT_ADMIN", saved.getRole());

        ArgumentCaptor<TenantAdminUsers> captor = ArgumentCaptor.forClass(TenantAdminUsers.class);
        verify(repository).save(captor.capture());
        assertNotNull(captor.getValue().getTenantId());
    }

    @Test
    void deveBloquearCriacaoQuandoTenantNaoEstiverAtivo() {
        TenantAdminUsersRequestDTO request = new TenantAdminUsersRequestDTO(
                1L,
                "Admin Tenant",
                "admin@tenant.com",
                "tenant.admin",
                "senhaForte123",
                "TENANT_ADMIN",
                "ATIVO"
        );
        Tenants tenant = criarTenant("SUSPENSO");

        when(tenantsRepository.findById(1L)).thenReturn(Optional.of(tenant));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido vincular admin a tenant que nao esteja ativo", exception.getMessage());
    }

    @Test
    void deveBloquearTrocaDeTenantAposCriacao() {
        TenantAdminUsers entity = new TenantAdminUsers();
        entity.setId(10L);
        entity.setTenantId(criarTenant("ATIVO"));
        entity.setLogin("tenant.admin");
        entity.setEmail("admin@tenant.com");
        entity.setRole("TENANT_ADMIN");
        entity.setStatus("ATIVO");
        entity.setUltimoAcesso((LocalDateTime) null);

        TenantAdminUsersRequestDTO request = new TenantAdminUsersRequestDTO(
                2L,
                "Admin Tenant",
                "admin@tenant.com",
                "tenant.admin",
                null,
                "TENANT_ADMIN",
                "ATIVO"
        );

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10L, request));

        assertEquals("Nao e permitido alterar o tenant do admin apos a criacao", exception.getMessage());
    }

    private Tenants criarTenant(String status) {
        Tenants tenant = new Tenants();
        tenant.setId(1L);
        tenant.setCodigo("TENANT-01");
        tenant.setStatus(status);
        return tenant;
    }
}
