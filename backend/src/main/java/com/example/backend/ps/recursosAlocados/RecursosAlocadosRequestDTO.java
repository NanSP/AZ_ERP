package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.tarefas.Tarefas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecursosAlocadosRequestDTO
        (
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
}
