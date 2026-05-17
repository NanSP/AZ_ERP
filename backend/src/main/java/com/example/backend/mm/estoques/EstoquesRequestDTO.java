package com.example.backend.mm.estoques;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EstoquesRequestDTO
        (
                Integer produto,
                Integer empresa,
                String localizacao,
                String lote,
                BigDecimal quantidade,
                BigDecimal quantidadeMinima,
                BigDecimal quantidadeMaxima,
                BigDecimal valorUnitario,
                LocalDate dataValidade,
                LocalDateTime createdAt
        ) {
}
