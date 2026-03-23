package com.example.backend.am.manutencoes;

import com.example.backend.am.bensPatrimoniais.BensPatrimoniais;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ManutencoesRequestDTO
        (
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
}
