package com.example.backend.fiscal.esocialEventos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class EsocialEventosService {

    private final EsocialEventosRepository repository;

    public EsocialEventosService(EsocialEventosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EsocialEventos criar(EsocialEventosRequestDTO data) {
        validar(data);
        validarEventoIdDuplicadoParaCriacao(normalizarOpcional(data.eventoId()));

        EsocialEventos entity = new EsocialEventos();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public EsocialEventos atualizar(Long id, EsocialEventosRequestDTO data) {
        validar(data);
        validarEventoIdDuplicadoParaAtualizacao(normalizarOpcional(data.eventoId()), id);

        EsocialEventos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento eSocial nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        EsocialEventos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento eSocial nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            EsocialEventos entity,
            EsocialEventosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setPeriodoApuracao(data.periodoApuracao());
        entity.setTipoEvento(normalizarTipoEvento(data.tipoEvento()));
        entity.setEventoId(normalizarOpcional(data.eventoId()));
        entity.setConteudo(normalizarObrigatorio(data.conteudo(), "Conteudo do evento e obrigatorio"));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(EsocialEventosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do evento eSocial sao obrigatorios");
        }

        if (data.periodoApuracao() == null) {
            throw new ValidacaoException("Periodo de apuracao e obrigatorio");
        }

        validarTipoEvento(normalizarTipoEvento(data.tipoEvento()));
        normalizarObrigatorio(data.conteudo(), "Conteudo do evento e obrigatorio");

        validarStatus(normalizarStatus(data.status()));
        validarEventoId(normalizarOpcional(data.eventoId()));
        validarStatusComFluxo(
                normalizarStatus(data.status()),
                normalizarOpcional(data.eventoId()),
                normalizarObrigatorio(data.conteudo(), "Conteudo do evento e obrigatorio"),
                data.periodoApuracao()
        );
    }

    private void validarTipoEvento(String tipoEvento) {
        if (!tipoEvento.matches("s\\d{4,6}")) {
            throw new ValidacaoException("Tipo de evento invalido");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("gerado")
                && !status.equals("enviado")
                && !status.equals("processado")
                && !status.equals("erro")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarEventoId(String eventoId) {
        if (eventoId == null) {
            return;
        }

        if (eventoId.length() > 40) {
            throw new ValidacaoException("Evento ID nao pode ultrapassar 40 caracteres");
        }
    }

    private void validarStatusComFluxo(
            String status,
            String eventoId,
            String conteudo,
            LocalDate periodoApuracao
    ) {
        if ((status.equals("enviado") || status.equals("processado")) && eventoId == null) {
            throw new ValidacaoException("Evento ID e obrigatorio para evento enviado ou processado");
        }

        if ((status.equals("enviado") || status.equals("processado")) && !pareceConteudoEstruturado(conteudo)) {
            throw new ValidacaoException("Conteudo estruturado e obrigatorio para evento enviado ou processado");
        }

        if (status.equals("processado") && periodoApuracao.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Evento processado nao pode ter periodo de apuracao futuro");
        }
    }

    private void validarExclusao(EsocialEventos entity) {
        String status = entity.getStatus() == null ? null : entity.getStatus().trim().toLowerCase();

        if ("enviado".equals(status) || "processado".equals(status)) {
            throw new ValidacaoException("Nao e permitido excluir evento eSocial em status avancado");
        }
    }

    private void validarEventoIdDuplicadoParaCriacao(String eventoId) {
        if (eventoId != null && repository.existsByEventoId(eventoId)) {
            throw new ValidacaoException("Ja existe evento eSocial com o evento ID informado");
        }
    }

    private void validarEventoIdDuplicadoParaAtualizacao(String eventoId, Long id) {
        if (eventoId != null && repository.existsByEventoIdAndIdNot(eventoId, id)) {
            throw new ValidacaoException("Ja existe evento eSocial com o evento ID informado");
        }
    }

    private String normalizarTipoEvento(String tipoEvento) {
        if (tipoEvento == null || tipoEvento.isBlank()) {
            throw new ValidacaoException("Tipo de evento e obrigatorio");
        }

        return tipoEvento.trim().toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "gerado" : valor.toLowerCase();
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

    private boolean pareceConteudoEstruturado(String conteudo) {
        return conteudo.contains("<") && conteudo.contains(">");
    }
}
