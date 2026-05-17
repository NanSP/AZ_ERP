package com.example.backend.ps.projetos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjetosRequestDTO
        (
                String codigo,
                String nome,
                String descricao,
                Integer cliente,
                Integer gerente,
                LocalDate dataInicio,
                LocalDate dataFim,
                LocalDate dataPrevistaInicio,
                LocalDate dataPrevistaFim,
                BigDecimal orcamentoTotal,
                BigDecimal orcamentoGasto,
                String status,
                Integer prioridade,
                LocalDateTime createdAt
        ) {
}
