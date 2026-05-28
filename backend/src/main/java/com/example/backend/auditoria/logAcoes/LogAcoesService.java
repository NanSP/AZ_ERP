package com.example.backend.auditoria.logAcoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LogAcoesService {

    private final LogAcoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public LogAcoesService(
            LogAcoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public LogAcoes criar(LogAcoesRequestDTO data) {
        validar(data);

        Usuarios usuario = buscarUsuarioOpcional(data.usuario());

        LogAcoes entity = new LogAcoes();
        preencher(entity, data, usuario, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public LogAcoes atualizar(Long id, LogAcoesRequestDTO data) {
        throw new ValidacaoException("Log de acao nao pode ser alterado");
    }

    @Transactional
    public void excluir(Long id) {
        LogAcoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Log de acao nao encontrado"));

        throw new ValidacaoException("Log de acao nao pode ser excluido");
    }

    private void preencher(
            LogAcoes entity,
            LogAcoesRequestDTO data,
            Usuarios usuario,
            LocalDateTime createdAt
    ) {
        entity.setUsuario(usuario);
        entity.setModulo(normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio"));
        entity.setAcao(normalizarObrigatorio(data.acao(), "Acao e obrigatoria"));
        entity.setTabela(normalizarObrigatorio(data.tabela(), "Tabela e obrigatoria"));
        entity.setRegistroId(data.registroId());
        entity.setDadosAntigos(data.dadosAntigos());
        entity.setDadosNovos(data.dadosNovos());
        entity.setIpAddress(data.ipAddress());
        entity.setUserAgent(normalizarOpcional(data.userAgent()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(LogAcoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do log de acao sao obrigatorios");
        }

        normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio");
        String acao = normalizarObrigatorio(data.acao(), "Acao e obrigatoria");
        normalizarObrigatorio(data.tabela(), "Tabela e obrigatoria");

        validarAcao(acao);
        validarContextoDaAcao(acao, data.registroId(), data.dadosAntigos(), data.dadosNovos());
    }

    private void validarAcao(String acao) {
        String valor = acao.toLowerCase();

        if (!valor.equals("insert")
                && !valor.equals("update")
                && !valor.equals("delete")
                && !valor.equals("login")
                && !valor.equals("logout")) {
            throw new ValidacaoException("Acao invalida");
        }
    }

    private void validarContextoDaAcao(
            String acao,
            Integer registroId,
            Map<String, Object> dadosAntigos,
            Map<String, Object> dadosNovos
    ) {
        boolean acaoDeSessao = acao.equalsIgnoreCase("login") || acao.equalsIgnoreCase("logout");

        if (!acaoDeSessao && registroId == null) {
            throw new ValidacaoException("Registro ID e obrigatorio para esta acao");
        }

        if (!acaoDeSessao
                && (dadosAntigos == null || dadosAntigos.isEmpty())
                && (dadosNovos == null || dadosNovos.isEmpty())) {
            throw new ValidacaoException("Ao menos um entre dados antigos e dados novos deve ser informado para esta acao");
        }
    }

    private Usuarios buscarUsuarioOpcional(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }

        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
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
