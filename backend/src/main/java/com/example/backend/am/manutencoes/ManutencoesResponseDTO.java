package com.example.backend.am.manutencoes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ManutencoesResponseDTO
        (
                Integer id,
                Integer ativo,
                String tipoManutencao,
                LocalDate dataSolicitacao,
                LocalDate dataExecucao,
                String descricao,
                BigDecimal custoMaoObra,
                BigDecimal custoMaterial,
                BigDecimal custoTotal,
                Integer tecnico,
                LocalDateTime createdAt
        ) {
    public ManutencoesResponseDTO(Manutencoes manutencoes) {
        this
                (
                        manutencoes.getId(),
                        manutencoes.getAtivo() != null ? manutencoes.getAtivo().getId() : null,
                        manutencoes.getTipoManutencao(),
                        manutencoes.getDataSolicitacao(),
                        manutencoes.getDataExecucao(),
                        manutencoes.getDescricao(),
                        manutencoes.getCustoMaoObra(),
                        manutencoes.getCustoMaterial(),
                        manutencoes.getCustoTotal(),
                        manutencoes.getTecnico() != null ? manutencoes.getTecnico().getId() : null,
                        manutencoes.getCreatedAt()
                );
    }
}
