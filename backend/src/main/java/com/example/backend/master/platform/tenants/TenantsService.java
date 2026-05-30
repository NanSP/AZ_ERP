package com.example.backend.master.platform.tenants;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsersRepository;
import com.example.backend.master.platform.tenantDatabases.TenantDatabasesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TenantsService {

    private final TenantsRepository repository;
    private final TenantDatabasesRepository tenantDatabasesRepository;
    private final TenantAdminUsersRepository tenantAdminUsersRepository;
    private final ProvisioningLogsRepository provisioningLogsRepository;

    public TenantsService(
            TenantsRepository repository,
            TenantDatabasesRepository tenantDatabasesRepository,
            TenantAdminUsersRepository tenantAdminUsersRepository,
            ProvisioningLogsRepository provisioningLogsRepository
    ) {
        this.repository = repository;
        this.tenantDatabasesRepository = tenantDatabasesRepository;
        this.tenantAdminUsersRepository = tenantAdminUsersRepository;
        this.provisioningLogsRepository = provisioningLogsRepository;
    }

    @Transactional
    public Tenants criar(TenantsRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarObrigatorio(data.codigo(), "Codigo do tenant e obrigatorio"));
        validarDocumentoDuplicadoParaCriacao(normalizarDocumentoOpcional(data.documento()));

        Tenants entity = new Tenants();
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public Tenants atualizar(Long id, TenantsRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(
                normalizarObrigatorio(data.codigo(), "Codigo do tenant e obrigatorio"),
                id
        );
        validarDocumentoDuplicadoParaAtualizacao(
                normalizarDocumentoOpcional(data.documento()),
                id
        );

        Tenants entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Tenants entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    @Transactional
    public Tenants atualizarStatusProvisionamento(Long id, String status) {
        Tenants entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));

        entity.setStatus(normalizarStatus(status));
        validarStatus(entity.getStatus());

        return repository.save(entity);
    }

    private void preencher(Tenants entity, TenantsRequestDTO data) {
        entity.setCodigo(normalizarObrigatorio(data.codigo(), "Codigo do tenant e obrigatorio"));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do tenant e obrigatorio"));
        entity.setNomeFantasia(normalizarOpcional(data.nomeFantasia()));
        entity.setDocumento(normalizarDocumentoOpcional(data.documento()));
        entity.setTipoDocumento(normalizarTipoDocumento(data.tipoDocumento(), data.documento()));
        entity.setEmailResponsavel(normalizarEmailOpcional(data.emailResponsavel()));
        entity.setTelefoneResponsavel(normalizarOpcional(data.telefoneResponsavel()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setPlano(normalizarPlano(data.plano()));
        entity.setSchemaVersion(normalizarSchemaVersion(data.schemaVersion()));
        entity.setObservacoes(normalizarObservacoes(data.observacoes()));
    }

    private void validar(TenantsRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do tenant sao obrigatorios");
        }

        normalizarObrigatorio(data.codigo(), "Codigo do tenant e obrigatorio");
        normalizarObrigatorio(data.nome(), "Nome do tenant e obrigatorio");

        String documento = normalizarDocumentoOpcional(data.documento());
        String tipoDocumento = normalizarTipoDocumento(data.tipoDocumento(), data.documento());
        String emailResponsavel = normalizarEmailOpcional(data.emailResponsavel());

        validarStatus(normalizarStatus(data.status()));
        validarDocumento(documento, tipoDocumento);
        validarEmailResponsavel(emailResponsavel);
        validarPlano(normalizarPlano(data.plano()));
        validarSchemaVersion(normalizarSchemaVersion(data.schemaVersion()));
        validarObservacoes(normalizarObservacoes(data.observacoes()));
    }

    private void validarAlteracoesSensiveis(Tenants entity, TenantsRequestDTO data) {
        if (!tenantEmUso(entity.getId())) {
            return;
        }

        String novoCodigo = normalizarObrigatorio(data.codigo(), "Codigo do tenant e obrigatorio");
        String novoDocumento = normalizarDocumentoOpcional(data.documento());
        String novoTipoDocumento = normalizarTipoDocumento(data.tipoDocumento(), data.documento());
        String novoStatus = normalizarStatus(data.status());
        String novoPlano = normalizarPlano(data.plano());
        String novaSchemaVersion = normalizarSchemaVersion(data.schemaVersion());

        if (!novoCodigo.equals(entity.getCodigo())) {
            throw new ValidacaoException("Nao e permitido alterar o codigo do tenant apos o provisionamento");
        }

        if (entity.getDocumento() == null ? novoDocumento != null : !entity.getDocumento().equals(novoDocumento)) {
            throw new ValidacaoException("Nao e permitido alterar o documento do tenant apos o provisionamento");
        }

        if (entity.getTipoDocumento() == null ? novoTipoDocumento != null : !entity.getTipoDocumento().equals(novoTipoDocumento)) {
            throw new ValidacaoException("Nao e permitido alterar o tipo de documento do tenant apos o provisionamento");
        }

        if (tenantEmUso(entity.getId()) && !novoStatus.equals(entity.getStatus())) {
            throw new ValidacaoException("Nao e permitido alterar o status do tenant manualmente apos provisionamento");
        }

        if (tenantEmUso(entity.getId()) && !novoPlano.equals(entity.getPlano())) {
            throw new ValidacaoException("Nao e permitido alterar o plano do tenant apos o provisionamento");
        }

        if (tenantEmUso(entity.getId())
                && (entity.getSchemaVersion() == null
                ? novaSchemaVersion != null
                : !entity.getSchemaVersion().equals(novaSchemaVersion))) {
            throw new ValidacaoException("Nao e permitido alterar a schema version do tenant apos o provisionamento");
        }
    }

    private void validarExclusao(Tenants entity) {
        if (tenantEmUso(entity.getId())) {
            throw new ValidacaoException("Nao e permitido excluir tenant com infraestrutura ou historico associado");
        }
    }

    private boolean tenantEmUso(Long tenantId) {
        return tenantDatabasesRepository.existsByTenantIdId(tenantId)
                || tenantAdminUsersRepository.existsByTenantIdId(tenantId)
                || provisioningLogsRepository.existsByTenantIdId(tenantId);
    }

    private void validarStatus(String status) {
        if (!status.equals("PENDENTE")
                && !status.equals("ATIVO")
                && !status.equals("SUSPENSO")
                && !status.equals("INATIVO")) {
            throw new ValidacaoException("Status do tenant invalido");
        }
    }

    private void validarDocumento(String documento, String tipoDocumento) {
        if (documento == null) {
            return;
        }

        if (tipoDocumento.equals("CPF") && !documento.matches("\\d{11}")) {
            throw new ValidacaoException("CPF do tenant deve conter 11 digitos numericos");
        }

        if (tipoDocumento.equals("CNPJ") && !documento.matches("\\d{14}")) {
            throw new ValidacaoException("CNPJ do tenant deve conter 14 digitos numericos");
        }
    }

    private void validarEmailResponsavel(String email) {
        if (email != null && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidacaoException("Email do responsavel invalido");
        }
    }

    private void validarPlano(String plano) {
        if (!plano.equals("STARTER")
                && !plano.equals("PROFESSIONAL")
                && !plano.equals("ENTERPRISE")) {
            throw new ValidacaoException("Plano do tenant invalido");
        }
    }

    private void validarSchemaVersion(String schemaVersion) {
        if (schemaVersion != null && !schemaVersion.matches("^V\\d+$")) {
            throw new ValidacaoException("Schema version invalida");
        }
    }

    private void validarObservacoes(String observacoes) {
        if (observacoes != null && observacoes.length() > 1000) {
            throw new ValidacaoException("Observacoes do tenant nao podem exceder 1000 caracteres");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe tenant com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Long id) {
        if (repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe tenant com o codigo informado");
        }
    }

    private void validarDocumentoDuplicadoParaCriacao(String documento) {
        if (documento != null && repository.existsByDocumento(documento)) {
            throw new ValidacaoException("Ja existe tenant com o documento informado");
        }
    }

    private void validarDocumentoDuplicadoParaAtualizacao(String documento, Long id) {
        if (documento != null && repository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ValidacaoException("Ja existe tenant com o documento informado");
        }
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "PENDENTE" : valor.toUpperCase();
    }

    private String normalizarTipoDocumento(String tipoDocumento, String documento) {
        String valor = normalizarOpcional(tipoDocumento);
        if (valor == null && documento == null) {
            return null;
        }
        if (valor == null) {
            throw new ValidacaoException("Tipo de documento e obrigatorio quando houver documento");
        }
        return valor.toUpperCase();
    }

    private String normalizarPlano(String plano) {
        String valor = normalizarOpcional(plano);
        return valor == null ? "STARTER" : valor.toUpperCase();
    }

    private String normalizarSchemaVersion(String schemaVersion) {
        String valor = normalizarOpcional(schemaVersion);
        return valor == null ? "V1" : valor.toUpperCase();
    }

    private String normalizarDocumentoOpcional(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }
        return documento.replaceAll("\\D", "");
    }

    private String normalizarEmailOpcional(String email) {
        String valor = normalizarOpcional(email);
        return valor == null ? null : valor.toLowerCase();
    }

    private String normalizarObservacoes(String observacoes) {
        return normalizarOpcional(observacoes);
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
}
