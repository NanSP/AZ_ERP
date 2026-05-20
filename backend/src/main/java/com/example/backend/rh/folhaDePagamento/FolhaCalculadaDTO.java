package com.example.backend.rh.folhaDePagamento;

import java.math.BigDecimal;

public record FolhaCalculadaDTO(
        BigDecimal valorHora,
        BigDecimal valorHorasNormais,
        BigDecimal valorHorasExtras,
        BigDecimal valorBruto,
        BigDecimal valorLiquido
) {
}
