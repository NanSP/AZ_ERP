package ps.projetos;

import core.parceiros.Parceiros;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjetosResponseDTO
        (
                Integer id,
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
        )
        {
                public ProjetosResponseDTO(Projetos projetos) {
                        this(
                                projetos.getId(),
                                projetos.getCodigo(),
                                projetos.getNome(),
                                projetos.getDescricao(),
                                projetos.getClienteId(),
                                projetos.getGerenteId(),
                                projetos.getDataInicio(),
                                projetos.getDataFim(),
                                projetos.getDataPrevistaInicio(),
                                projetos.getDataPrevistaFim(),
                                projetos.getOrcamentoTotal(),
                                projetos.getOrcamentoGasto(),
                                projetos.getStatus(),
                                projetos.getPrioridade(),
                                projetos.getCreatedAt()
                        );
                }
}
