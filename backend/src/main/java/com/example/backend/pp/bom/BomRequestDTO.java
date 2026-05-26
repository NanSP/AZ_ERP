package com.example.backend.pp.bom;

import java.math.BigDecimal;

public record BomRequestDTO
        (
                Integer produtoPai,
                Integer componente,
                BigDecimal quantidade,
                String unidadeMedida,
                Integer nivel,
                BigDecimal tempoPreparacao,
                BigDecimal tempoProducao,
                Integer roteiro
        ) {
}
