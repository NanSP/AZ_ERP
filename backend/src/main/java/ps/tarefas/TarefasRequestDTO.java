package ps.tarefas;

import ps.projetos.Projetos;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TarefasRequestDTO
        (
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
}
