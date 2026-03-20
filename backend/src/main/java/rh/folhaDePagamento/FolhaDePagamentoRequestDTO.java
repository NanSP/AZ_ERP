package rh.folhaDePagamento;

import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FolhaDePagamentoRequestDTO
        (
                Colaboradores colaboradorId,
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
