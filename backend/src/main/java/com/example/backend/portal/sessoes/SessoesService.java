package com.example.backend.portal.sessoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessoesService {

    private final SessoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public SessoesService(
            SessoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Sessoes criar(SessoesRequestDTO data) {
        validar(data);
        validarTokenDuplicadoParaCriacao(normalizarObrigatorio(data.tokenSessao(), "Token da sessao e obrigatorio"));
        validarSessaoAtivaParaCriacao(data.usuario(), data.dataLogout());

        Usuarios usuario = buscarUsuario(data.usuario());

        Sessoes entity = new Sessoes();
        preencher(entity, data, usuario, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Sessoes atualizar(Integer id, SessoesRequestDTO data) {
        validar(data);
        validarTokenDuplicadoParaAtualizacao(normalizarObrigatorio(data.tokenSessao(), "Token da sessao e obrigatorio"), id);
        validarSessaoAtivaParaAtualizacao(data.usuario(), data.dataLogout(), id);

        Sessoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));

        validarAtualizacao(entity, data);

        Usuarios usuario = buscarUsuario(data.usuario());
        preencher(entity, data, usuario, entity.getDataLogin());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Sessoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));

        throw new ValidacaoException("Sessao nao pode ser excluida");
    }

    private void preencher(
            Sessoes entity,
            SessoesRequestDTO data,
            Usuarios usuario,
            LocalDateTime dataLoginBase
    ) {
        LocalDateTime dataLogin = resolverDataLogin(data.dataLogin(), dataLoginBase);

        entity.setUsuario(usuario);
        entity.setTokenSessao(normalizarObrigatorio(data.tokenSessao(), "Token da sessao e obrigatorio"));
        entity.setIpAddress(data.ipAddress());
        entity.setUserAgent(normalizarOpcional(data.userAgent()));
        entity.setDataLogin(dataLogin);
        entity.setDataLogout(data.dataLogout());
        entity.setExpiracao(data.expiracao());
    }

    private void validar(SessoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da sessao sao obrigatorios");
        }

        if (data.usuario() == null) {
            throw new ValidacaoException("Usuario e obrigatorio");
        }

        normalizarObrigatorio(data.tokenSessao(), "Token da sessao e obrigatorio");
        if (data.ipAddress() == null) {
            throw new ValidacaoException("IP da sessao e obrigatorio");
        }

        normalizarObrigatorio(data.userAgent(), "User-Agent e obrigatorio");

        if (data.expiracao() == null) {
            throw new ValidacaoException("Expiracao da sessao e obrigatoria");
        }

        if (data.dataLogin() != null
                && data.dataLogout() != null
                && data.dataLogout().isBefore(data.dataLogin())) {
            throw new ValidacaoException("Data de logout nao pode ser anterior a data de login");
        }

        if (data.dataLogin() != null
                && data.expiracao() != null
                && data.expiracao().isBefore(data.dataLogin())) {
            throw new ValidacaoException("Expiracao nao pode ser anterior a data de login");
        }
    }

    private void validarAtualizacao(Sessoes entity, SessoesRequestDTO data) {
        if (entity.getDataLogout() != null && data.dataLogout() == null) {
            throw new ValidacaoException("Sessao encerrada nao pode voltar a ficar aberta");
        }

        LocalDateTime dataLoginBase = entity.getDataLogin() != null
                ? entity.getDataLogin()
                : data.dataLogin();

        if (dataLoginBase != null
                && data.dataLogout() != null
                && data.dataLogout().isBefore(dataLoginBase)) {
            throw new ValidacaoException("Data de logout nao pode ser anterior a data de login");
        }

        if (dataLoginBase != null
                && data.expiracao() != null
                && data.expiracao().isBefore(dataLoginBase)) {
            throw new ValidacaoException("Expiracao nao pode ser anterior a data de login");
        }
    }

    private void validarTokenDuplicadoParaCriacao(String tokenSessao) {
        if (repository.existsByTokenSessao(tokenSessao)) {
            throw new ValidacaoException("Ja existe sessao com o token informado");
        }
    }

    private void validarTokenDuplicadoParaAtualizacao(String tokenSessao, Integer id) {
        if (repository.existsByTokenSessaoAndIdNot(tokenSessao, id)) {
            throw new ValidacaoException("Ja existe sessao com o token informado");
        }
    }

    private void validarSessaoAtivaParaCriacao(Integer usuarioId, LocalDateTime dataLogout) {
        if (usuarioId != null && dataLogout == null && repository.existsByUsuarioIdAndDataLogoutIsNull(usuarioId)) {
            throw new ValidacaoException("Usuario ja possui sessao ativa");
        }
    }

    private void validarSessaoAtivaParaAtualizacao(Integer usuarioId, LocalDateTime dataLogout, Integer id) {
        if (usuarioId != null
                && dataLogout == null
                && repository.existsByUsuarioIdAndDataLogoutIsNullAndIdNot(usuarioId, id)) {
            throw new ValidacaoException("Usuario ja possui outra sessao ativa");
        }
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private LocalDateTime resolverDataLogin(LocalDateTime dataRequest, LocalDateTime dataAtual) {
        if (dataAtual != null) {
            return dataAtual;
        }

        return dataRequest != null ? dataRequest : LocalDateTime.now();
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
