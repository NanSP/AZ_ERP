package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import org.springframework.stereotype.Service;

@Service
public class ProvisioningLogsService {

    private final ProvisioningLogsRepository provisioningLogsRepository;
    private final TenantsRepository tenantsRepository;
    private final SystemUsersRepository systemUsersRepository;

    public ProvisioningLogsService(
            ProvisioningLogsRepository provisioningLogsRepository,
            TenantsRepository tenantsRepository,
            SystemUsersRepository systemUsersRepository
    ) {
        this.provisioningLogsRepository = provisioningLogsRepository;
        this.tenantsRepository = tenantsRepository;
        this.systemUsersRepository = systemUsersRepository;
    }

    public ProvisioningLogs create(ProvisioningLogsRequestDTO data) {
        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        SystemUsers executadoPor = systemUsersRepository.findById(data.executadoPorId())
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado"));

        ProvisioningLogs entity = new ProvisioningLogs();
        entity.setTenantId(tenant);
        entity.setEtapa(data.etapa());
        entity.setStatus(data.status());
        entity.setMensagem(data.mensagem());
        entity.setDetalhes(data.detalhes());
        entity.setExecutadoPor(executadoPor);
        entity.setCreatedAt(data.createdAt());

        return provisioningLogsRepository.save(entity);
    }

    public ProvisioningLogs update(Long id, ProvisioningLogsRequestDTO data) {
        ProvisioningLogs entity = provisioningLogsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provisioning log não encontrado"));

        Tenants tenant = tenantsRepository.findById(data.tenantId())
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        SystemUsers executadoPor = systemUsersRepository.findById(data.executadoPorId())
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado"));

        entity.setTenantId(tenant);
        entity.setEtapa(data.etapa());
        entity.setStatus(data.status());
        entity.setMensagem(data.mensagem());
        entity.setDetalhes(data.detalhes());
        entity.setExecutadoPor(executadoPor);
        entity.setCreatedAt(data.createdAt());

        return provisioningLogsRepository.save(entity);
    }
}
