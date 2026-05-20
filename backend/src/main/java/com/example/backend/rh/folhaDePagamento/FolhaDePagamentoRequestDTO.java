package com.example.backend.rh.folhaDePagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FolhaDePagamentoRequestDTO
        (
                Integer colaborador,
                LocalDate competencia,
                BigDecimal salarioBase,
                BigDecimal horasNormais,
                BigDecimal horasExtras,
                BigDecimal adicionais,
                BigDecimal descontos,
                LocalDate dataPagamento,
                String status
        ) {
}
