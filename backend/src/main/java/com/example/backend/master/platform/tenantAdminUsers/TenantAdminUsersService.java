package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantAdminUsersService {

    private final TenantAdminUsersRepository tenantAdminUsersRepository;
    private final TenantsRepository tenantsRepository;

    public TenantAdminUsersService(
            TenantAdminUsersRepository tenantAdminUsersRepository,
            TenantsRepository tenantsRepository
    ) {
        this.tenantAdminUsersRepository = tenantAdminUsersRepository;
        this.tenantsRepository = tenantsRepository;
    }

    public TenantAdminUsers create(TenantAdminUsersRequestDTO data) {
        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        TenantAdminUsers entity = new TenantAdminUsers();
        entity.setTenantId(tenant);
        entity.setNome(data.nome());
        entity.setEmail(data.email());
        entity.setLogin(data.login());
        entity.setSenhaHash(data.senhaHash());
        entity.setRole(data.role());
        entity.setStatus(data.status());
        entity.setUltimoAcesso(data.ultimoAcesso());
        entity.setCreatedAt(data.createdAt());
        entity.setUpdatedAt(data.updatedAt());

        return tenantAdminUsersRepository.save(entity);
    }

    public TenantAdminUsers update(Long id, TenantAdminUsersRequestDTO data) {
        TenantAdminUsers entity = tenantAdminUsersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant admin user não encontrado"));

        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        entity.setTenantId(tenant);
        entity.setNome(data.nome());
        entity.setEmail(data.email());
        entity.setLogin(data.login());
        entity.setSenhaHash(data.senhaHash());
        entity.setRole(data.role());
        entity.setStatus(data.status());
        entity.setUltimoAcesso(data.ultimoAcesso());
        entity.setCreatedAt(data.createdAt());
        entity.setUpdatedAt(data.updatedAt());

        return tenantAdminUsersRepository.save(entity);
    }
}
