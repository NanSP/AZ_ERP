package com.example.backend.pp.ordemProducao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdemProducaoRequestDTO
        (
                String numeroOp,
                Integer produto,
                BigDecimal quantidadePlanejada,
                BigDecimal quantidadeProduzida,
                LocalDate dataEmissao,
                LocalDate dataInicio,
                LocalDate dataFim,
                LocalDate dataPrevista,
                String status,
                Integer prioridade,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
