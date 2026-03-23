package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.tarefas.Tarefas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecursosAlocadosResponseDTO
        (
                Integer id,
                Projetos projetoId,
                Tarefas tarefaId,
                String tipoRecurso,
                Integer recursoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                LocalDate dataAlocacao,
                LocalDateTime createdAt
        ) {
    public RecursosAlocadosResponseDTO(RecursosAlocados recursosAlocados) {
        this(
                recursosAlocados.getId(),
                recursosAlocados.getProjetoId(),
                recursosAlocados.getTarefaId(),
                recursosAlocados.getTipoRecurso(),
                recursosAlocados.getRecursoId(),
                recursosAlocados.getQuantidade(),
                recursosAlocados.getValorUnitario(),
                recursosAlocados.getValorTotal(),
                recursosAlocados.getDataAlocacao(),
                recursosAlocados.getCreatedAt()
        );
    }
}
