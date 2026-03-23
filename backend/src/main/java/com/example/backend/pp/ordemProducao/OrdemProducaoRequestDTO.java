package com.example.backend.pp.ordemProducao;

import com.example.backend.core.produtos.Produtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdemProducaoRequestDTO
        (
                String numeroOp,
                Produtos produtoId,
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
