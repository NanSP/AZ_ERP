package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.security.SensitiveDataSanitizer;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProvisioningLogsService {

    private final ProvisioningLogsRepository repository;
    private final TenantsRepository tenantsRepository;
    private final SystemUsersRepository systemUsersRepository;
    private final SensitiveDataSanitizer sensitiveDataSanitizer;

    public ProvisioningLogsService(
            ProvisioningLogsRepository provisioningLogsRepository,
            TenantsRepository tenantsRepository,
            SystemUsersRepository systemUsersRepository,
            SensitiveDataSanitizer sensitiveDataSanitizer
    ) {
        this.repository = provisioningLogsRepository;
        this.tenantsRepository = tenantsRepository;
        this.systemUsersRepository = systemUsersRepository;
        this.sensitiveDataSanitizer = sensitiveDataSanitizer;
    }

    public ProvisioningLogsService(
            ProvisioningLogsRepository provisioningLogsRepository,
            TenantsRepository tenantsRepository,
            SystemUsersRepository systemUsersRepository
    ) {
        this(provisioningLogsRepository, tenantsRepository, systemUsersRepository, null);
    }

    @Transactional
    public ProvisioningLogs criar(ProvisioningLogsRequestDTO data) {
        validar(data);

        Tenants tenant = buscarTenant(data.tenantId());
        SystemUsers executadoPor = buscarExecutor(data.executadoPorId());

        ProvisioningLogs entity = new ProvisioningLogs();
        preencher(entity, data, tenant, executadoPor);

        return repository.save(entity);
    }

    @Transactional
    public ProvisioningLogs atualizar(Long id, ProvisioningLogsRequestDTO data) {
        throw new ValidacaoException("Provisioning log nao pode ser alterado");
    }

    @Transactional
    public void excluir(Long id) {
        ProvisioningLogs entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Provisioning log nao encontrado"));

        throw new ValidacaoException("Provisioning log nao pode ser excluido");
    }

    private void preencher(
            ProvisioningLogs entity,
            ProvisioningLogsRequestDTO data,
            Tenants tenant,
            SystemUsers executadoPor
    ) {
        entity.setTenantId(tenant);
        entity.setEtapa(normalizarObrigatorio(data.etapa(), "Etapa e obrigatoria"));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setMensagem(normalizarObrigatorio(data.mensagem(), "Mensagem e obrigatoria"));
        entity.setDetalhes(normalizarDetalhes(data.detalhes()));
        entity.setExecutadoPor(executadoPor);
    }

    private void validar(ProvisioningLogsRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do provisioning log sao obrigatorios");
        }

        if (data.executadoPorId() == null) {
            throw new ValidacaoException("Usuario executor e obrigatorio");
        }

        validarRelacionamentoComTenant(buscarTenant(data.tenantId()));
        normalizarObrigatorio(data.etapa(), "Etapa e obrigatoria");
        normalizarObrigatorio(data.mensagem(), "Mensagem e obrigatoria");
        validarStatus(normalizarStatus(data.status()));
        validarDetalhes(normalizarDetalhes(data.detalhes()));
    }

    private void validarStatus(String status) {
        if (!status.equals("INFO")
                && !status.equals("SUCESSO")
                && !status.equals("ERRO")) {
            throw new ValidacaoException("Status do provisioning log invalido");
        }
    }

    private void validarDetalhes(Map<String, Object> detalhes) {
        if (detalhes == null) {
            return;
        }

        if (detalhes.size() > 100) {
            throw new ValidacaoException("Detalhes do provisioning log excedem o limite permitido");
        }
    }

    private Tenants buscarTenant(Long tenantId) {
        if (tenantId == null) {
            return null;
        }

        return tenantsRepository.findById(tenantId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));
    }

    private SystemUsers buscarExecutor(Long executadoPorId) {
        return systemUsersRepository.findById(executadoPorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario executor nao encontrado"));
    }

    private void validarRelacionamentoComTenant(Tenants tenant) {
        if (tenant != null
                && !"PENDENTE".equalsIgnoreCase(tenant.getStatus())
                && !"ATIVO".equalsIgnoreCase(tenant.getStatus())
                && !"SUSPENSO".equalsIgnoreCase(tenant.getStatus())) {
            throw new ValidacaoException("Nao e permitido registrar provisioning log para tenant fora do ciclo operacional");
        }
    }

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ValidacaoException("Status e obrigatorio");
        }

        return status.trim().toUpperCase();
    }

    private Map<String, Object> normalizarDetalhes(Map<String, Object> detalhes) {
        return sensitiveDataSanitizer != null ? sensitiveDataSanitizer.sanitizeMap(detalhes) : detalhes;
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }
}
