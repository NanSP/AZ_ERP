package com.example.backend.rh.folhaDePagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FolhaDePagamentoResponseDTO
        (
                Integer id,
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
        )
    {
        public FolhaDePagamentoResponseDTO(FolhaDePagamento folhaDePagamento){
            this(
                    folhaDePagamento.getId(),
                    folhaDePagamento.getColaborador() != null ? folhaDePagamento.getColaborador().getId() : null,
                    folhaDePagamento.getCompetencia(),
                    folhaDePagamento.getSalarioBase(),
                    folhaDePagamento.getHorasNormais(),
                    folhaDePagamento.getHorasExtras(),
                    folhaDePagamento.getAdicionais(),
                    folhaDePagamento.getDescontos(),
                    folhaDePagamento.getValorLiquido(),
                    folhaDePagamento.getDataPagamento(),
                    folhaDePagamento.getStatus(),
                    folhaDePagamento.getCreatedAt()
            );
        }
}
