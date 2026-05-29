package com.example.backend.bi.metricas;

import java.math.BigDecimal;

public record MetricasRequestDTO
        (
                String nome,
                String descricao,
                String categoria,
                String formula,
                String unidadeMedida,
                BigDecimal meta
        ) {
}
