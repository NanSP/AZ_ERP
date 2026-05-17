package com.example.backend.grc.controles;

import java.time.LocalDateTime;

public record ControlesResponseDTO
        (
                Integer id,
                String codigo,
                String descricao,
                String tipoControle,
                String frequencia,
                Integer responsavel,
                Boolean efetivo,
                LocalDateTime createdAt
        ) {
    public ControlesResponseDTO(Controles controles) {
        this
                (
                        controles.getId(),
                        controles.getCodigo(),
                        controles.getDescricao(),
                        controles.getTipoControle(),
                        controles.getFrequencia(),
                        controles.getResponsavel() != null ? controles.getResponsavel().getId() : null,
                        controles.getEfetivo(),
                        controles.getCreatedAt()
                );
    }
}
