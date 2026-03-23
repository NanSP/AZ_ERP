package ps.projetos;

import core.parceiros.Parceiros;
import ps.tarefas.Tarefas;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjetosRequestDTO
        (
                List<Tarefas> tarefas,
                String codigo,
                String nome,
                String descricao,
                Parceiros clienteId,
                Usuarios gerenteId,
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
