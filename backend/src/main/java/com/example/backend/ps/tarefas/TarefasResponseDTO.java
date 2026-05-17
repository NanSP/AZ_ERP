package com.example.backend.ps.tarefas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TarefasResponseDTO
        (
                Integer id,
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
    public TarefasResponseDTO(Tarefas tarefas) {
        this(
                tarefas.getId(),
                tarefas.getProjeto() != null ? tarefas.getProjeto().getId() : null,
                tarefas.getTarefaPai() != null ? tarefas.getTarefaPai().getId() : null,
                tarefas.getTitulo(),
                tarefas.getDescricao(),
                tarefas.getResponsavel() != null ? tarefas.getResponsavel().getId() : null,
                tarefas.getDataInicio(),
                tarefas.getDataFim(),
                tarefas.getHorasEstimadas(),
                tarefas.getHorasRealizadas(),
                tarefas.getPercentualConcluido(),
                tarefas.getStatus(),
                tarefas.getPrioridade(),
                tarefas.getCreatedAt()
        );
    }
}
