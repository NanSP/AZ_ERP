package com.example.backend.fi.fluxoCaixa;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FluxoCaixaRequestDTO
        (
                LocalDate dataReferencia,
                BigDecimal saldoInicial,
                BigDecimal entradasPrevistas,
                BigDecimal saidasPrevistas,
                BigDecimal entradasRealizadas,
                BigDecimal saidasRealizadas
        ) {
}
