package com.example.backend.fi.fluxoCaixa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FluxoCaixaRequestDTO
        (
                LocalDate dataReferencia,
                BigDecimal saldoInicial,
                BigDecimal entradasPrevistas,
                BigDecimal saidasPrevistas,
                BigDecimal entradasRealizadas,
                BigDecimal saidasRealizadas,
                BigDecimal saldoFinalPrevisto,
                BigDecimal saldoFinalReal,
                LocalDateTime createdAt
        ) {
}
