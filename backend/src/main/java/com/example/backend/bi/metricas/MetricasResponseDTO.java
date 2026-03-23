package com.example.backend.bi.metricas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MetricasResponseDTO
        (
                Integer id,
                String nome,
                String descricao,
                String categoria,
                String formula,
                String unidadeMedida,
                BigDecimal meta,
                LocalDateTime createdAt
        ) {
    public MetricasResponseDTO(Metricas metricas) {
        this
                (
                        metricas.getId(),
                        metricas.getNome(),
                        metricas.getDescricao(),
                        metricas.getCategoria(),
                        metricas.getFormula(),
                        metricas.getUnidadeMedida(),
                        metricas.getMeta(),
                        metricas.getCreatedAt()
                );
    }
}
