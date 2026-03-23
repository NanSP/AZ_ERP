package com.example.backend.am.manutencoes;

import com.example.backend.am.bensPatrimoniais.BensPatrimoniais;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ManutencoesResponseDTO
        (
                Integer id,
                BensPatrimoniais ativoId,
                String tipoManutencao,
                LocalDate dataSolicitacao,
                LocalDate dataExecucao,
                String descricao,
                BigDecimal custoMaoObra,
                BigDecimal custoMaterial,
                BigDecimal custoTotal,
                Colaboradores tecnicoId,
                LocalDateTime createdAt
        ) {
    public ManutencoesResponseDTO(Manutencoes manutencoes) {
        this
                (
                        manutencoes.getId(),
                        manutencoes.getAtivoId(),
                        manutencoes.getTipoManutencao(),
                        manutencoes.getDataSolicitacao(),
                        manutencoes.getDataExecucao(),
                        manutencoes.getDescricao(),
                        manutencoes.getCustoMaoObra(),
                        manutencoes.getCustoMaterial(),
                        manutencoes.getCustoTotal(),
                        manutencoes.getTecnicoId(),
                        manutencoes.getCreatedAt()
                );
    }
}
