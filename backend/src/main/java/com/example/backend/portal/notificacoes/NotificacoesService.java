package com.example.backend.portal.notificacoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificacoesService {

    private final NotificacoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public NotificacoesService(
            NotificacoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Notificacoes criar(NotificacoesRequestDTO data) {
        validar(data);
        validarDuplicidadeAtivaParaCriacao(data);

        Usuarios usuario = buscarUsuario(data.usuario());

        Notificacoes entity = new Notificacoes();
        preencher(entity, data, usuario, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Notificacoes atualizar(Integer id, NotificacoesRequestDTO data) {
        validar(data);
        validarDuplicidadeAtivaParaAtualizacao(data, id);

        Notificacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificacao nao encontrada"));

        Usuarios usuario = buscarUsuario(data.usuario());
        preencher(entity, data, usuario, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Notificacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificacao nao encontrada"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Notificacoes entity,
            NotificacoesRequestDTO data,
            Usuarios usuario,
            LocalDateTime createdAt
    ) {
        entity.setUsuario(usuario);
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo da notificacao e obrigatorio"));
        entity.setMensagem(normalizarObrigatorio(data.mensagem(), "Mensagem da notificacao e obrigatoria"));
        entity.setTipo(normalizarTipo(data.tipo()));
        entity.setLida(normalizarLida(data.lida()));
        entity.setDataLeitura(resolverDataLeitura(data.lida(), data.dataLeitura(), entity.getDataLeitura()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(NotificacoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da notificacao sao obrigatorios");
        }

        if (data.usuario() == null) {
            throw new ValidacaoException("Usuario e obrigatorio");
        }

        normalizarObrigatorio(data.titulo(), "Titulo da notificacao e obrigatorio");
        normalizarObrigatorio(data.mensagem(), "Mensagem da notificacao e obrigatoria");

        validarTipo(normalizarTipo(data.tipo()));
        validarLeitura(normalizarLida(data.lida()), data.dataLeitura());
    }

    private void validarTipo(String tipo) {
        if (tipo == null) {
            return;
        }

        if (!tipo.equals("info")
                && !tipo.equals("sucesso")
                && !tipo.equals("alerta")
                && !tipo.equals("erro")) {
            throw new ValidacaoException("Tipo de notificacao invalido");
        }
    }

    private void validarLeitura(Boolean lida, LocalDateTime dataLeitura) {
        if (Boolean.TRUE.equals(lida) && dataLeitura == null) {
            throw new ValidacaoException("Data de leitura e obrigatoria quando a notificacao estiver lida");
        }

        if (Boolean.FALSE.equals(lida) && dataLeitura != null) {
            throw new ValidacaoException("Notificacao nao lida nao deve possuir data de leitura");
        }
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private Boolean normalizarLida(Boolean lida) {
        return lida != null && lida;
    }

    private String normalizarTipo(String tipo) {
        String valor = normalizarOpcional(tipo);
        return valor == null ? "info" : valor.toLowerCase();
    }

    private LocalDateTime resolverDataLeitura(Boolean lida, LocalDateTime dataRequest, LocalDateTime dataAtual) {
        if (!Boolean.TRUE.equals(normalizarLida(lida))) {
            return null;
        }

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

    private void validarDuplicidadeAtivaParaCriacao(NotificacoesRequestDTO data) {
        if (Boolean.TRUE.equals(normalizarLida(data.lida()))) {
            return;
        }

        String titulo = normalizarObrigatorio(data.titulo(), "Titulo da notificacao e obrigatorio");
        String mensagem = normalizarObrigatorio(data.mensagem(), "Mensagem da notificacao e obrigatoria");
        String tipo = normalizarTipo(data.tipo());

        if (repository.existsByUsuarioIdAndTituloAndMensagemAndTipoAndLidaFalse(
                data.usuario(),
                titulo,
                mensagem,
                tipo
        )) {
            throw new ValidacaoException("Ja existe notificacao nao lida com o mesmo contexto para este usuario");
        }
    }

    private void validarDuplicidadeAtivaParaAtualizacao(NotificacoesRequestDTO data, Integer id) {
        if (Boolean.TRUE.equals(normalizarLida(data.lida()))) {
            return;
        }

        String titulo = normalizarObrigatorio(data.titulo(), "Titulo da notificacao e obrigatorio");
        String mensagem = normalizarObrigatorio(data.mensagem(), "Mensagem da notificacao e obrigatoria");
        String tipo = normalizarTipo(data.tipo());

        if (repository.existsByUsuarioIdAndTituloAndMensagemAndTipoAndLidaFalseAndIdNot(
                data.usuario(),
                titulo,
                mensagem,
                tipo,
                id
        )) {
            throw new ValidacaoException("Ja existe notificacao nao lida com o mesmo contexto para este usuario");
        }
    }

    private void validarExclusao(Notificacoes entity) {
        if (Boolean.TRUE.equals(entity.getLida()) || entity.getDataLeitura() != null) {
            throw new ValidacaoException("Nao e permitido excluir notificacao que ja foi lida");
        }
    }
}
