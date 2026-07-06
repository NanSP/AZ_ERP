package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.backend.security.SecurityUserPrincipal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SolicitacaoTitularEventosService {

    private final SolicitacaoTitularEventoRepository repository;
    private final SolicitacoesTitularRepository solicitacoesTitularRepository;
    private final UsuariosRepository usuariosRepository;

    public SolicitacaoTitularEventosService(
            SolicitacaoTitularEventoRepository repository,
            SolicitacoesTitularRepository solicitacoesTitularRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.solicitacoesTitularRepository = solicitacoesTitularRepository;
        this.usuariosRepository = usuariosRepository;
    }

    public List<SolicitacaoTitularEvento> listarPorSolicitacao(Integer solicitacaoId) {
        garantirSolicitacao(solicitacaoId);
        return repository.findBySolicitacaoIdOrderByCreatedAtDesc(solicitacaoId);
    }

    @Transactional
    public SolicitacaoTitularEvento registrarEventoManual(Integer solicitacaoId, SolicitacaoTitularEventoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do evento sao obrigatorios");
        }

        SolicitacoesTitular solicitacao = garantirSolicitacao(solicitacaoId);
        String tipoEvento = normalizarObrigatorio(data.tipoEvento(), "Tipo do evento e obrigatorio");
        String titulo = normalizarObrigatorio(data.titulo(), "Titulo do evento e obrigatorio");

        SolicitacaoTitularEvento entity = new SolicitacaoTitularEvento();
        entity.setSolicitacao(solicitacao);
        entity.setTipoEvento(tipoEvento.toLowerCase());
        entity.setTitulo(titulo);
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setDetalhesJson(data.detalhesJson());
        entity.setCriadoPor(usuarioAutenticado());
        entity.setCreatedAt(LocalDateTime.now());
        return repository.save(entity);
    }

    @Transactional
    public void registrarEventoAutomatico(
            SolicitacoesTitular solicitacao,
            String tipoEvento,
            String titulo,
            String descricao,
            Map<String, Object> detalhesJson
    ) {
        SolicitacaoTitularEvento entity = new SolicitacaoTitularEvento();
        entity.setSolicitacao(solicitacao);
        entity.setTipoEvento(tipoEvento);
        entity.setTitulo(titulo);
        entity.setDescricao(descricao);
        entity.setDetalhesJson(detalhesJson);
        entity.setCriadoPor(usuarioAutenticado());
        entity.setCreatedAt(LocalDateTime.now());
        repository.save(entity);
    }

    private SolicitacoesTitular garantirSolicitacao(Integer solicitacaoId) {
        return solicitacoesTitularRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao do titular nao encontrada"));
    }

    private Usuarios usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUserPrincipal principal)) {
            return null;
        }

        if (principal.getUserId() == null) {
            return null;
        }

        return usuariosRepository.findById(principal.getUserId().intValue()).orElse(null);
    }

    private String normalizarObrigatorio(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidacaoException(message);
        }

        return value.trim();
    }

    private String normalizarOpcional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
