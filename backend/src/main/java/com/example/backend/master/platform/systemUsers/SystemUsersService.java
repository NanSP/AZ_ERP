package com.example.backend.master.platform.systemUsers;

import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SystemUsersService {

    private final SystemUsersRepository repository;
    private final ProvisioningLogsRepository provisioningLogsRepository;
    private final PasswordEncoder passwordEncoder;

    public SystemUsersService(
            SystemUsersRepository repository,
            ProvisioningLogsRepository provisioningLogsRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.provisioningLogsRepository = provisioningLogsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SystemUsers criar(SystemUsersRequestDTO data) {
        validar(data, true);
        validarEmailDuplicadoParaCriacao(normalizarEmail(data.email()));
        validarLoginDuplicadoParaCriacao(normalizarLogin(data.login()));

        SystemUsers entity = new SystemUsers();
        preencher(entity, data, true);

        return repository.save(entity);
    }

    @Transactional
    public SystemUsers atualizar(Long id, SystemUsersRequestDTO data) {
        validar(data, false);

        SystemUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("System user nao encontrado"));

        validarEmailDuplicadoParaAtualizacao(normalizarEmail(data.email()), id);
        validarLoginDuplicadoParaAtualizacao(normalizarLogin(data.login()), id);
        validarAlteracoesSensiveis(entity, data);

        preencher(entity, data, false);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        SystemUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("System user nao encontrado"));

        if (provisioningLogsRepository.existsByExecutadoPorId(id)) {
            throw new ValidacaoException("System user nao pode ser excluido pois possui logs vinculados");
        }

        repository.delete(entity);
    }

    private void preencher(SystemUsers entity, SystemUsersRequestDTO data, boolean criar) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome e obrigatorio"));
        entity.setEmail(normalizarEmail(data.email()));
        entity.setLogin(normalizarLogin(data.login()));
        entity.setRole(normalizarRole(data.role()));
        entity.setStatus(normalizarStatus(data.status()));

        if (criar || (data.senha() != null && !data.senha().isBlank())) {
            entity.setSenha(passwordEncoder.encode(data.senha()));
            entity.setPasswordChangeRequired(true);
            entity.setPasswordChangedAt(null);
        }
    }

    private void validar(SystemUsersRequestDTO data, boolean criar) {
        if (data == null) {
            throw new ValidacaoException("Dados do system user sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome e obrigatorio");
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

    private void validarAlteracoesSensiveis(SystemUsers entity, SystemUsersRequestDTO data) {
        String novoEmail = normalizarEmail(data.email());
        String novoLogin = normalizarLogin(data.login());
        String novaRole = normalizarRole(data.role());
        String novoStatus = normalizarStatus(data.status());
        boolean possuiUsoOperacional = entity.getUltimoAcesso() != null
                || provisioningLogsRepository.existsByExecutadoPorId(entity.getId());

        if (possuiUsoOperacional && !novoEmail.equals(entity.getEmail())) {
            throw new ValidacaoException("Nao e permitido alterar o email apos uso operacional");
        }

        if (entity.getUltimoAcesso() != null) {
            if (!novoLogin.equals(entity.getLogin())) {
                throw new ValidacaoException("Nao e permitido alterar o login apos uso operacional");
            }

            if (!novaRole.equals(entity.getRole())) {
                throw new ValidacaoException("Nao e permitido alterar a role apos uso operacional");
            }
        }

        if (possuiUsoOperacional && !novoStatus.equals(entity.getStatus())) {
            throw new ValidacaoException("Nao e permitido alterar o status do system user apos uso operacional");
        }
    }

    private void validarEmailDuplicadoParaCriacao(String email) {
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ValidacaoException("Ja existe system user com o email informado");
        }
    }

    private void validarEmailDuplicadoParaAtualizacao(String email, Long id) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ValidacaoException("Ja existe system user com o email informado");
        }
    }

    private void validarLoginDuplicadoParaCriacao(String login) {
        if (repository.existsByLoginIgnoreCase(login)) {
            throw new ValidacaoException("Ja existe system user com o login informado");
        }
    }

    private void validarLoginDuplicadoParaAtualizacao(String login, Long id) {
        if (repository.existsByLoginIgnoreCaseAndIdNot(login, id)) {
            throw new ValidacaoException("Ja existe system user com o login informado");
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
        if (!role.equals("ADMIN_SISTEMA")
                && !role.equals("SUPORTE")
                && !role.equals("OPERADOR")) {
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

        return switch (role.trim().toUpperCase()) {
            case "MASTER_ADMIN" -> "ADMIN_SISTEMA";
            case "SUPPORT" -> "SUPORTE";
            case "OPERATIONS" -> "OPERADOR";
            default -> role.trim().toUpperCase();
        };
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
