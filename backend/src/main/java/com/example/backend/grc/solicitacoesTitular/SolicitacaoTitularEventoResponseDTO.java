package com.example.backend.grc.solicitacoesTitular;

import java.time.LocalDateTime;
import java.util.Map;

public record SolicitacaoTitularEventoResponseDTO(
        Integer id,
        Integer solicitacaoId,
        String tipoEvento,
        String titulo,
        String descricao,
        Map<String, Object> detalhesJson,
        Integer criadoPorId,
        LocalDateTime createdAt
) {
    public SolicitacaoTitularEventoResponseDTO(SolicitacaoTitularEvento entity) {
        this(
                entity.getId(),
                entity.getSolicitacao() != null ? entity.getSolicitacao().getId() : null,
                entity.getTipoEvento(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDetalhesJson(),
                entity.getCriadoPor() != null ? entity.getCriadoPor().getId() : null,
                entity.getCreatedAt()
        );
    }
}
