package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import com.example.backend.master.platform.tenants.TenantsRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TenantAdminUsersService {

    private final TenantAdminUsersRepository repository;
    private final TenantsRepository tenantsRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantAdminUsersService(
            TenantAdminUsersRepository repository,
            TenantsRepository tenantsRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.tenantsRepository = tenantsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TenantAdminUsers criar(TenantAdminUsersRequestDTO data) {
        validar(data, true);
        validarEmailDuplicadoParaCriacao(normalizarEmail(data.email()));
        validarLoginDuplicadoParaCriacao(normalizarLogin(data.login()));

        Tenants tenant = buscarTenant(data.tenantId());
        validarRelacionamentoComTenant(tenant);

        TenantAdminUsers entity = new TenantAdminUsers();
        preencher(entity, data, tenant, true);

        return repository.save(entity);
    }

    @Transactional
    public TenantAdminUsers atualizar(Long id, TenantAdminUsersRequestDTO data) {
        validar(data, false);

        TenantAdminUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant admin user nao encontrado"));

        validarEmailDuplicadoParaAtualizacao(normalizarEmail(data.email()), id);
        validarLoginDuplicadoParaAtualizacao(normalizarLogin(data.login()), id);
        validarAlteracoesSensiveis(entity, data);

        Tenants tenant = buscarTenant(data.tenantId());
        validarRelacionamentoComTenant(tenant);
        preencher(entity, data, tenant, false);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        TenantAdminUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant admin user nao encontrado"));

        throw new ValidacaoException("Tenant admin user nao pode ser excluido");
    }

    private void preencher(
            TenantAdminUsers entity,
            TenantAdminUsersRequestDTO data,
            Tenants tenant,
            boolean criar
    ) {
        entity.setTenantId(tenant);
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do admin e obrigatorio"));
        entity.setEmail(normalizarEmail(data.email()));
        entity.setLogin(normalizarLogin(data.login()));
        entity.setRole(normalizarRole(data.role()));
        entity.setStatus(normalizarStatus(data.status()));

        if (criar || (data.senha() != null && !data.senha().isBlank())) {
            entity.setSenha(passwordEncoder.encode(data.senha()));
        }
    }

    private void validar(TenantAdminUsersRequestDTO data, boolean criar) {
        if (data == null) {
            throw new ValidacaoException("Dados do tenant admin user sao obrigatorios");
        }

        if (data.tenantId() == null) {
            throw new ValidacaoException("Tenant e obrigatorio");
        }

        normalizarObrigatorio(data.nome(), "Nome do admin e obrigatorio");
        validarEmail(normalizarEmail(data.email()));
        validarLogin(normalizarLogin(data.login()));
        validarRole(normalizarRole(data.role()));
        validarStatus(normalizarStatus(data.status()));

        if (criar && (data.senha() == null || data.senha().isBlank())) {
            throw new ValidacaoException("Senha e obrigatoria");
        }

        if (data.senha() != null && !data.senha().isBlank() && data.senha().trim().length() < 8) {
            throw new ValidacaoException("Senha deve ter pelo menos 8 caracteres");
        }
    }

    private void validarAlteracoesSensiveis(TenantAdminUsers entity, TenantAdminUsersRequestDTO data) {
        Long tenantAtualId = entity.getTenantId() != null ? entity.getTenantId().getId() : null;
        Long novoTenantId = data.tenantId();
        String novoLogin = normalizarLogin(data.login());
        String novaRole = normalizarRole(data.role());
        String novoStatus = normalizarStatus(data.status());

        if (tenantAtualId != null && !tenantAtualId.equals(novoTenantId)) {
            throw new ValidacaoException("Nao e permitido alterar o tenant do admin apos a criacao");
        }

        if (entity.getUltimoAcesso() != null) {
            if (!novoLogin.equals(entity.getLogin())) {
                throw new ValidacaoException("Nao e permitido alterar o login do admin apos uso operacional");
            }

            if (!novaRole.equals(entity.getRole())) {
                throw new ValidacaoException("Nao e permitido alterar a role do admin apos uso operacional");
            }
        }

        if ("ATIVO".equals(entity.getStatus()) && "INATIVO".equals(novoStatus) && entity.getUltimoAcesso() != null) {
            throw new ValidacaoException("Nao e permitido inativar manualmente admin que ja possui acesso operacional");
        }
    }

    private void validarEmailDuplicadoParaCriacao(String email) {
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ValidacaoException("Ja existe tenant admin user com o email informado");
        }
    }

    private void validarEmailDuplicadoParaAtualizacao(String email, Long id) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ValidacaoException("Ja existe tenant admin user com o email informado");
        }
    }

    private void validarLoginDuplicadoParaCriacao(String login) {
        if (repository.existsByLoginIgnoreCase(login)) {
            throw new ValidacaoException("Ja existe tenant admin user com o login informado");
        }
    }

    private void validarLoginDuplicadoParaAtualizacao(String login, Long id) {
        if (repository.existsByLoginIgnoreCaseAndIdNot(login, id)) {
            throw new ValidacaoException("Ja existe tenant admin user com o login informado");
        }
    }

    private Tenants buscarTenant(Long tenantId) {
        return tenantsRepository.findById(tenantId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));
    }

    private void validarRelacionamentoComTenant(Tenants tenant) {
        if (tenant != null && !"ATIVO".equalsIgnoreCase(tenant.getStatus())) {
            throw new ValidacaoException("Nao e permitido vincular admin a tenant que nao esteja ativo");
        }
    }

    private void validarEmail(String email) {
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidacaoException("Email invalido");
        }
    }

    private void validarLogin(String login) {
        if (login.length() < 3) {
            throw new ValidacaoException("Login deve ter pelo menos 3 caracteres");
        }
    }

    private void validarRole(String role) {
        if (!role.equals("MASTER_ADMIN")
                && !role.equals("TENANT_ADMIN")
                && !role.equals("SUPPORT")) {
            throw new ValidacaoException("Role invalida");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("ATIVO")
                && !status.equals("INATIVO")
                && !status.equals("SUSPENSO")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidacaoException("Email e obrigatorio");
        }

        return email.trim().toLowerCase();
    }

    private String normalizarLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new ValidacaoException("Login e obrigatorio");
        }

        return login.trim().toLowerCase();
    }

    private String normalizarRole(String role) {
        if (role == null || role.isBlank()) {
            throw new ValidacaoException("Role e obrigatoria");
        }

        return role.trim().toUpperCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "ATIVO" : valor.toUpperCase();
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
