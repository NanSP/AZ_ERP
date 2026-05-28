package com.example.backend.auditoria.logErros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LogErrosService {

    private final LogErrosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public LogErrosService(
            LogErrosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public LogErros criar(LogErrosRequestDTO data) {
        validar(data);
        validarDuplicidadeRecente(data);

        Usuarios usuario = buscarUsuarioOpcional(data.usuario());

        LogErros entity = new LogErros();
        preencher(entity, data, usuario, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public LogErros atualizar(Long id, LogErrosRequestDTO data) {
        throw new ValidacaoException("Log de erro nao pode ser alterado");
    }

    @Transactional
    public void excluir(Long id) {
        LogErros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Log de erro nao encontrado"));

        throw new ValidacaoException("Log de erro nao pode ser excluido");
    }

    private void preencher(
            LogErros entity,
            LogErrosRequestDTO data,
            Usuarios usuario,
            LocalDateTime createdAt
    ) {
        entity.setErroCodigo(data.erroCodigo());
        entity.setErroMensagem(normalizarObrigatorio(data.erroMensagem(), "Mensagem do erro e obrigatoria"));
        entity.setModulo(normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio"));
        entity.setUsuario(usuario);
        entity.setUrl(normalizarOpcional(data.url()));
        entity.setParametros(data.parametros());
        entity.setIpAddress(data.ipAddress());
        entity.setCreatedAt(createdAt);
    }

    private void validar(LogErrosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do log de erro sao obrigatorios");
        }

        normalizarObrigatorio(data.erroMensagem(), "Mensagem do erro e obrigatoria");
        normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio");

        if (data.erroCodigo() != null && data.erroCodigo() < 0) {
            throw new ValidacaoException("Codigo do erro nao pode ser negativo");
        }

        validarContexto(data.url(), data.parametros());
        validarContextoDeRastreabilidade(data.usuario(), data.ipAddress(), data.url());
    }

    private void validarContexto(String url, Map<String, Object> parametros) {
        String urlNormalizada = normalizarOpcional(url);

        if (urlNormalizada != null && !urlValida(urlNormalizada)) {
            throw new ValidacaoException("URL do log deve ser um caminho relativo ou uma URL absoluta valida");
        }
    }

    private void validarDuplicidadeRecente(LogErrosRequestDTO data) {
        String modulo = normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio");
        String erroMensagem = normalizarObrigatorio(data.erroMensagem(), "Mensagem do erro e obrigatoria");
        String url = normalizarOpcional(data.url());
        LocalDateTime limiteRecente = LocalDateTime.now().minusMinutes(1);

        if (repository.existsByModuloAndErroMensagemAndUrlAndCreatedAtAfter(
                modulo,
                erroMensagem,
                url,
                limiteRecente
        )) {
            throw new ValidacaoException("Ja existe log de erro identico registrado recentemente");
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

    private void validarContextoDeRastreabilidade(Integer usuarioId, java.net.InetAddress ipAddress, String url) {
        String urlNormalizada = normalizarOpcional(url);

        if (usuarioId != null && ipAddress == null) {
            throw new ValidacaoException("IP deve ser informado quando houver usuario no log de erro");
        }

        if (ipAddress != null && urlNormalizada == null) {
            throw new ValidacaoException("URL deve ser informada quando houver IP no log de erro");
        }
    }

    private boolean urlValida(String url) {
        if (url.startsWith("/")) {
            return true;
        }

        try {
            URI uri = new URI(url);
            return uri.isAbsolute();
        } catch (URISyntaxException ex) {
            return false;
        }
    }
}
