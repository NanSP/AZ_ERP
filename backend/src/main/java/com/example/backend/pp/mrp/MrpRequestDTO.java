package com.example.backend.pp.mrp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MrpRequestDTO
        (
                Integer produto,
                LocalDate periodo,
                BigDecimal demandaPrevista,
                BigDecimal estoqueAtual,
                BigDecimal estoqueSeguranca,
                BigDecimal necessidadeCompra,
                BigDecimal necessidadeProducao,
                LocalDate dataNecessidade,
                LocalDateTime createdAt
        ) {
}
