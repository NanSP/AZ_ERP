package com.example.backend.sys.usuarios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarioPerfil.UsuarioPerfilRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UsuariosService {

    private final UsuariosRepository repository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuariosService(
            UsuariosRepository repository,
            UsuarioPerfilRepository usuarioPerfilRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuarios criar(UsuariosRequestDTO data) {
        validar(data, true);
        validarEmailDuplicadoParaCriacao(normalizarObrigatorio(data.email(), "Email e obrigatorio"));
        validarLoginDuplicadoParaCriacao(normalizarObrigatorio(data.login(), "Login e obrigatorio"));

        Usuarios entity = new Usuarios();
        preencher(entity, data, LocalDateTime.now(), true);

        return repository.save(entity);
    }

    @Transactional
    public Usuarios atualizar(Integer id, UsuariosRequestDTO data) {
        validar(data, false);
        validarEmailDuplicadoParaAtualizacao(normalizarObrigatorio(data.email(), "Email e obrigatorio"), id);
        validarLoginDuplicadoParaAtualizacao(normalizarObrigatorio(data.login(), "Login e obrigatorio"), id);

        Usuarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt(), false);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Usuarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (usuarioPerfilRepository.existsByUsuarioId(id)) {
            throw new ValidacaoException("Nao e permitido excluir usuario que possui perfis vinculados");
        }

        repository.delete(entity);
    }

    private void preencher(
            Usuarios entity,
            UsuariosRequestDTO data,
            LocalDateTime createdAt,
            boolean criar
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome e obrigatorio"));
        entity.setEmail(normalizarObrigatorio(data.email(), "Email e obrigatorio"));
        entity.setLogin(normalizarObrigatorio(data.login(), "Login e obrigatorio"));
        entity.setDocumento(normalizarOpcional(data.documento()));
        entity.setTipoUsuario(normalizarTipoUsuario(data.tipoUsuario()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setUltimoAcesso(resolverUltimoAcesso(entity.getUltimoAcesso(), criar));
        entity.setExpiracaoSenha(data.expiracaoSenha());
        entity.setTentativasLogin(normalizarTentativasLogin(data.tentativasLogin()));
        entity.setCreatedAt(createdAt);

        if (criar || (data.senha() != null && !data.senha().isBlank())) {
            entity.setSenhaHash(passwordEncoder.encode(data.senha()));
        }
    }

    private void validar(UsuariosRequestDTO data, boolean criar) {
        if (data == null) {
            throw new ValidacaoException("Dados do usuario sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome e obrigatorio");
        String email = normalizarObrigatorio(data.email(), "Email e obrigatorio");
        String login = normalizarObrigatorio(data.login(), "Login e obrigatorio");

        validarEmail(email);
        validarLogin(login);
        validarStatus(normalizarStatus(data.status()));
        validarTipoUsuario(normalizarTipoUsuario(data.tipoUsuario()));

        if (criar && (data.senha() == null || data.senha().isBlank())) {
            throw new ValidacaoException("Senha e obrigatoria");
        }

        if (data.senha() != null && !data.senha().isBlank() && data.senha().trim().length() < 6) {
            throw new ValidacaoException("Senha deve ter pelo menos 6 caracteres");
        }

        if (normalizarTentativasLogin(data.tentativasLogin()) < 0) {
            throw new ValidacaoException("Tentativas de login nao podem ser negativas");
        }

        if (data.expiracaoSenha() != null && data.expiracaoSenha().isBefore(LocalDate.now().minusYears(1))) {
            throw new ValidacaoException("Expiracao de senha invalida");
        }
    }

    private void validarAlteracoesSensiveis(Usuarios entity, UsuariosRequestDTO data) {
        if (!usuarioPerfilRepository.existsByUsuarioId(entity.getId())) {
            return;
        }

        String novoStatus = normalizarStatus(data.status());
        String novoTipoUsuario = normalizarTipoUsuario(data.tipoUsuario());
        Integer novasTentativasLogin = normalizarTentativasLogin(data.tentativasLogin());

        if (!novoStatus.equals(entity.getStatus())) {
            throw new ValidacaoException("Nao e permitido alterar o status de usuario que possui perfis vinculados");
        }

        if (!novoTipoUsuario.equals(entity.getTipoUsuario())) {
            throw new ValidacaoException("Nao e permitido alterar o tipo de usuario que possui perfis vinculados");
        }

        if (!novasTentativasLogin.equals(entity.getTentativasLogin())) {
            throw new ValidacaoException("Nao e permitido alterar tentativas de login de usuario que possui perfis vinculados");
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

    private void validarStatus(String status) {
        if (!status.equals("ativo")
                && !status.equals("inativo")
                && !status.equals("bloqueado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarTipoUsuario(String tipoUsuario) {
        if (!tipoUsuario.equals("admin")
                && !tipoUsuario.equals("gestor")
                && !tipoUsuario.equals("operador")
                && !tipoUsuario.equals("cliente")) {
            throw new ValidacaoException("Tipo de usuario invalido");
        }
    }

    private void validarEmailDuplicadoParaCriacao(String email) {
        if (repository.existsByEmail(email)) {
            throw new ValidacaoException("Ja existe usuario com o email informado");
        }
    }

    private void validarEmailDuplicadoParaAtualizacao(String email, Integer id) {
        if (repository.existsByEmailAndIdNot(email, id)) {
            throw new ValidacaoException("Ja existe usuario com o email informado");
        }
    }

    private void validarLoginDuplicadoParaCriacao(String login) {
        if (repository.existsByLogin(login)) {
            throw new ValidacaoException("Ja existe usuario com o login informado");
        }
    }

    private void validarLoginDuplicadoParaAtualizacao(String login, Integer id) {
        if (repository.existsByLoginAndIdNot(login, id)) {
            throw new ValidacaoException("Ja existe usuario com o login informado");
        }
    }

    private String normalizarTipoUsuario(String tipoUsuario) {
        String valor = normalizarOpcional(tipoUsuario);
        return valor == null ? "operador" : valor.toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "ativo" : valor.toLowerCase();
    }

    private Integer normalizarTentativasLogin(Integer tentativasLogin) {
        return tentativasLogin == null ? 0 : tentativasLogin;
    }

    private LocalDateTime resolverUltimoAcesso(LocalDateTime ultimoAcessoAtual, boolean criar) {
        if (criar) {
            return null;
        }

        return ultimoAcessoAtual;
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
