package com.example.backend.am.manutencoes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ManutencoesRequestDTO
        (
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
}
