package ps.recursosAlocados;

import ps.projetos.Projetos;
import ps.tarefas.Tarefas;

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
