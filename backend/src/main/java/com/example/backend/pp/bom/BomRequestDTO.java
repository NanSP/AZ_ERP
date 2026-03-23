package com.example.backend.pp.bom;

import com.example.backend.core.produtos.Produtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BomRequestDTO
        (
                Produtos produtoPaiId,
                Produtos componenteId,
                BigDecimal quantidade,
                String unidadeMedida,
                Integer nivel,
                BigDecimal tempoPreparacao,
                BigDecimal tempoProducao,
                Integer roteiroId,
                LocalDateTime createdAt
        ) {
}
