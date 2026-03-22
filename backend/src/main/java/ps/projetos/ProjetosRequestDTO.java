package ps.projetos;

import core.parceiros.Parceiros;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjetosRequestDTO
        (
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
