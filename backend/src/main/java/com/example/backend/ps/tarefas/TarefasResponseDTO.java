package com.example.backend.ps.tarefas;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TarefasResponseDTO
        (
                Integer id,
                Projetos projetoId,
                Tarefas tarefaPaiId,
                List<Tarefas> subtarefas,
                String titulo,
                String descricao,
                Usuarios responsavelId,
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
                tarefas.getProjetoId(),
                tarefas.getTarefaPaiId(),
                tarefas.getSubtarefas(),
                tarefas.getTitulo(),
                tarefas.getDescricao(),
                tarefas.getResponsavelId(),
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
