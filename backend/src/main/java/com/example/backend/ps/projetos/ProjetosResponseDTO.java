package com.example.backend.ps.projetos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjetosResponseDTO
        (
                Integer id,
                String codigo,
                String nome,
                String descricao,
                Integer cliente,
                Integer gerente,
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
                                projetos.getCliente() != null ? projetos.getCliente().getId() : null,
                                projetos.getGerente() != null ? projetos.getGerente().getId() : null,
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
