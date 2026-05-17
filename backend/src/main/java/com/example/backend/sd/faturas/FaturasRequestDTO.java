package com.example.backend.sd.faturas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturasRequestDTO
        (
                Integer pedido,
                String numeroFatura,
                LocalDate dataEmissao,
                BigDecimal valorTotal,
                LocalDate dataVencimento,
                String status,
                LocalDateTime createdAt
        ) {
}
