package com.example.backend.pp.bom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BomRequestDTO
        (
                Integer produtoPai,
                Integer componente,
                BigDecimal quantidade,
                String unidadeMedida,
                Integer nivel,
                BigDecimal tempoPreparacao,
                BigDecimal tempoProducao,
                Integer roteiro,
                LocalDateTime createdAt
        ) {
}
