package com.example.backend.pp.mrp;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MrpRequestDTO
        (
                Integer produto,
                LocalDate periodo,
                BigDecimal demandaPrevista,
                BigDecimal estoqueAtual,
                BigDecimal estoqueSeguranca,
                LocalDate dataNecessidade
        ) {
}
