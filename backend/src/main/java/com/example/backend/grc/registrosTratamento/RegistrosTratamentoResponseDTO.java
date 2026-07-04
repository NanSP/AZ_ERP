package com.example.backend.grc.registrosTratamento;

import java.time.LocalDateTime;

public record RegistrosTratamentoResponseDTO(
        Integer id,
        String modulo,
        String entidade,
        String finalidade,
        String baseLegal,
        String categoriaTitular,
        String categoriaDados,
        Integer retencaoDias,
        String compartilhamento,
        Boolean requerConsentimento,
        Boolean ativo,
        Integer responsavel,
        String observacoes,
        LocalDateTime createdAt
) {
    public RegistrosTratamentoResponseDTO(RegistrosTratamento entity) {
        this(
                entity.getId(),
                entity.getModulo(),
                entity.getEntidade(),
                entity.getFinalidade(),
                entity.getBaseLegal(),
                entity.getCategoriaTitular(),
                entity.getCategoriaDados(),
                entity.getRetencaoDias(),
                entity.getCompartilhamento(),
                entity.getRequerConsentimento(),
                entity.getAtivo(),
                entity.getResponsavel() != null ? entity.getResponsavel().getId() : null,
                entity.getObservacoes(),
                entity.getCreatedAt()
        );
    }
}
