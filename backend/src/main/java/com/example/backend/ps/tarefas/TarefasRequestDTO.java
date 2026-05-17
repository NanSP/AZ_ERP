package com.example.backend.ps.tarefas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TarefasRequestDTO
        (
                Integer projeto,
                Integer tarefaPai,
                String titulo,
                String descricao,
                Integer responsavel,
                LocalDate dataInicio,
                LocalDate dataFim,
                BigDecimal horasEstimadas,
                BigDecimal horasRealizadas,
                Integer percentualConcluido,
                String status,
                Integer prioridade,
                LocalDateTime createdAt
        ) {
}
