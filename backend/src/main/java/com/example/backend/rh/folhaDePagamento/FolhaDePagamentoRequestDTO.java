package com.example.backend.rh.folhaDePagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FolhaDePagamentoRequestDTO
        (
                Integer colaborador,
                LocalDate competencia,
                BigDecimal salarioBase,
                BigDecimal horasNormais,
                BigDecimal horasExtras,
                BigDecimal adicionais,
                BigDecimal descontos,
                BigDecimal valorLiquido,
                LocalDate dataPagamento,
                String status,
                LocalDateTime createdAt
        ) {
}
