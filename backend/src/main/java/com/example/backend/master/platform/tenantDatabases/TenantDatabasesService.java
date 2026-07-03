package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.templateMigration.TemplateMigrationProperties;
import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.security.SensitiveDataCipherService;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TenantDatabasesService {

    private final TenantDatabasesRepository repository;
    private final TenantsRepository tenantsRepository;
    private final TemplateMigrationProperties templateMigrationProperties;
    private final SensitiveDataCipherService sensitiveDataCipherService;

    @Autowired
    public TenantDatabasesService(
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsRepository tenantsRepository,
            TemplateMigrationProperties templateMigrationProperties,
            SensitiveDataCipherService sensitiveDataCipherService
    ) {
        this.repository = tenantDatabasesRepository;
        this.tenantsRepository = tenantsRepository;
        this.templateMigrationProperties = templateMigrationProperties;
        this.sensitiveDataCipherService = sensitiveDataCipherService;
    }

    public TenantDatabasesService(
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantsRepository tenantsRepository,
            TemplateMigrationProperties templateMigrationProperties
    ) {
        this(tenantDatabasesRepository, tenantsRepository, templateMigrationProperties, null);
    }

    @Transactional
    public TenantDatabases criar(TenantDatabasesRequestDTO data) {
        TenantDatabasesRequestDTO normalizedData = normalizarCamposGerenciados(data);
        validar(normalizedData);
        validarStatusManual(normalizarProvisionStatus(normalizedData.provisionStatus()));
        validarDatabaseNameDuplicadoParaCriacao(normalizarObrigatorio(normalizedData.databaseName(), "Database name e obrigatorio"));

        Tenants tenant = buscarTenant(normalizedData.tenantId());

        TenantDatabases entity = new TenantDatabases();
        preencher(entity, normalizedData, tenant);

        return repository.save(entity);
    }

    @Transactional
    public TenantDatabases atualizar(Long id, TenantDatabasesRequestDTO data) {
        TenantDatabasesRequestDTO normalizedData = normalizarCamposGerenciados(data);
        validar(normalizedData);

        TenantDatabases entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant database nao encontrado"));

        validarPromocaoManualDeStatus(entity, normalizedData);
        validarDatabaseNameDuplicadoParaAtualizacao(normalizarObrigatorio(normalizedData.databaseName(), "Database name e obrigatorio"), id);
        validarAlteracoesSensiveis(entity, normalizedData);

        Tenants tenant = buscarTenant(normalizedData.tenantId());
        preencher(entity, normalizedData, tenant);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        TenantDatabases entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant database nao encontrado"));

        throw new ValidacaoException("Tenant database nao pode ser excluido");
    }

    @Transactional
    public TenantDatabases atualizarStatusProvisionamento(Long id, String provisionStatus, LocalDateTime lastCheckAt) {
        TenantDatabases entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant database nao encontrado"));

        entity.setProvisionStatus(normalizarProvisionStatus(provisionStatus));
        validarProvisionStatus(entity.getProvisionStatus());
        entity.setLastCheckAt(lastCheckAt);

        if ("ATIVO".equals(entity.getProvisionStatus()) && entity.getProvisionedAt() == null) {
            entity.setProvisionedAt(LocalDateTime.now());
        }

        return repository.save(entity);
    }

    private void preencher(
            TenantDatabases entity,
            TenantDatabasesRequestDTO data,
            Tenants tenant
    ) {
        entity.setTenantId(tenant);
        entity.setDatabaseName(normalizarObrigatorio(data.databaseName(), "Database name e obrigatorio"));
        entity.setTemplateName(normalizarObrigatorio(data.templateName(), "Template name e obrigatorio"));
        entity.setDbHost(normalizarObrigatorio(data.dbHost(), "DB host e obrigatorio"));
        entity.setDbPort(normalizarDbPort(data.dbPort()));
        entity.setDbUsername(normalizarObrigatorio(data.dbUsername(), "DB username e obrigatorio"));
        entity.setDbPassword(encryptSensitive(normalizarObrigatorio(data.dbPassword(), "DB password e obrigatorio")));
        entity.setProvisionStatus(normalizarProvisionStatus(data.provisionStatus()));
        entity.setLastCheckAt(data.lastCheckAt());

        if ("ATIVO".equals(entity.getProvisionStatus()) && entity.getProvisionedAt() == null) {
            entity.setProvisionedAt(LocalDateTime.now());
        }
    }

    private void validar(TenantDatabasesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do tenant database sao obrigatorios");
        }

        if (data.tenantId() == null) {
            throw new ValidacaoException("Tenant e obrigatorio");
        }

        normalizarObrigatorio(data.databaseName(), "Database name e obrigatorio");
        normalizarObrigatorio(data.templateName(), "Template name e obrigatorio");
        normalizarObrigatorio(data.dbHost(), "DB host e obrigatorio");
        normalizarObrigatorio(data.dbUsername(), "DB username e obrigatorio");
        normalizarObrigatorio(data.dbPassword(), "DB password e obrigatorio");

        String provisionStatus = normalizarProvisionStatus(data.provisionStatus());
        validarProvisionStatus(provisionStatus);
        validarDbPort(normalizarDbPort(data.dbPort()));
    }

    private TenantDatabasesRequestDTO normalizarCamposGerenciados(TenantDatabasesRequestDTO data) {
        if (data == null) {
            return null;
        }

        return new TenantDatabasesRequestDTO(
                data.tenantId(),
                data.databaseName(),
                resolverCampoGerenciado(data.templateName(), templateMigrationProperties.getDatabase()),
                resolverCampoGerenciado(data.dbHost(), templateMigrationProperties.getHost()),
                data.dbPort() == null ? templateMigrationProperties.getPort() : data.dbPort(),
                resolverCampoGerenciado(data.dbUsername(), templateMigrationProperties.getUsername()),
                resolverCampoGerenciado(data.dbPassword(), templateMigrationProperties.getPassword()),
                data.provisionStatus(),
                data.lastCheckAt()
        );
    }

    private void validarAlteracoesSensiveis(TenantDatabases entity, TenantDatabasesRequestDTO data) {
        String novoDatabaseName = normalizarObrigatorio(data.databaseName(), "Database name e obrigatorio");
        String novoTemplateName = normalizarObrigatorio(data.templateName(), "Template name e obrigatorio");
        String novoStatus = normalizarProvisionStatus(data.provisionStatus());
        String novoDbHost = normalizarObrigatorio(data.dbHost(), "DB host e obrigatorio");
        Integer novaDbPort = normalizarDbPort(data.dbPort());
        String novoDbUsername = normalizarObrigatorio(data.dbUsername(), "DB username e obrigatorio");
        String novaDbPassword = normalizarObrigatorio(data.dbPassword(), "DB password e obrigatorio");
        String dbPasswordAtual = decryptSensitive(entity.getDbPassword());

        if ("ATIVO".equals(entity.getProvisionStatus()) || entity.getProvisionedAt() != null) {
            if (!novoDatabaseName.equals(entity.getDatabaseName())) {
                throw new ValidacaoException("Nao e permitido alterar database name apos provisionamento");
            }

            if (!novoTemplateName.equals(entity.getTemplateName())) {
                throw new ValidacaoException("Nao e permitido alterar template name apos provisionamento");
            }

            if (!novoDbHost.equals(entity.getDbHost())) {
                throw new ValidacaoException("Nao e permitido alterar DB host apos provisionamento");
            }

            if (!novaDbPort.equals(entity.getDbPort())) {
                throw new ValidacaoException("Nao e permitido alterar DB port apos provisionamento");
            }

            if (!novoDbUsername.equals(entity.getDbUsername())) {
                throw new ValidacaoException("Nao e permitido alterar DB username apos provisionamento");
            }

            if (!novaDbPassword.equals(dbPasswordAtual)) {
                throw new ValidacaoException("Nao e permitido alterar DB password apos provisionamento");
            }
        }

        if ("ATIVO".equals(entity.getProvisionStatus()) && !novoStatus.equals(entity.getProvisionStatus())) {
            throw new ValidacaoException("Nao e permitido alterar manualmente o status de database ja ativa");
        }
    }

    private void validarProvisionStatus(String provisionStatus) {
        if (!provisionStatus.equals("PENDENTE")
                && !provisionStatus.equals("ATIVO")
                && !provisionStatus.equals("ERRO")
                && !provisionStatus.equals("SUSPENSO")) {
            throw new ValidacaoException("Provision status invalido");
        }
    }

    private void validarStatusManual(String provisionStatus) {
        if ("ATIVO".equals(provisionStatus)) {
            throw new ValidacaoException("Provision status ATIVO e reservado ao provisionamento automatico");
        }
    }

    private void validarPromocaoManualDeStatus(TenantDatabases entity, TenantDatabasesRequestDTO data) {
        String novoStatus = normalizarProvisionStatus(data.provisionStatus());
        if (!"ATIVO".equals(entity.getProvisionStatus()) && "ATIVO".equals(novoStatus)) {
            throw new ValidacaoException("Provision status ATIVO e reservado ao provisionamento automatico");
        }
    }

    private void validarDbPort(Integer dbPort) {
        if (dbPort < 1 || dbPort > 65535) {
            throw new ValidacaoException("DB port invalida");
        }
    }

    private void validarDatabaseNameDuplicadoParaCriacao(String databaseName) {
        if (repository.existsByDatabaseNameIgnoreCase(databaseName)) {
            throw new ValidacaoException("Ja existe tenant database com o database name informado");
        }
    }

    private void validarDatabaseNameDuplicadoParaAtualizacao(String databaseName, Long id) {
        if (repository.existsByDatabaseNameIgnoreCaseAndIdNot(databaseName, id)) {
            throw new ValidacaoException("Ja existe tenant database com o database name informado");
        }
    }

    private Tenants buscarTenant(Long tenantId) {
        return tenantsRepository.findById(tenantId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));
    }

    private String normalizarProvisionStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "PENDENTE" : valor.toUpperCase();
    }

    private Integer normalizarDbPort(Integer dbPort) {
        return dbPort == null ? 5432 : dbPort;
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private String resolverCampoGerenciado(String valor, String fallback) {
        String normalizado = normalizarOpcional(valor);
        if (normalizado == null
                || normalizado.equalsIgnoreCase("Gerenciado pelo servidor")
                || normalizado.equals("********")) {
            return fallback;
        }
        return normalizado;
    }

    private String encryptSensitive(String value) {
        return sensitiveDataCipherService != null ? sensitiveDataCipherService.encrypt(value) : value;
    }

    private String decryptSensitive(String value) {
        return sensitiveDataCipherService != null ? sensitiveDataCipherService.decrypt(value) : value;
    }
}
