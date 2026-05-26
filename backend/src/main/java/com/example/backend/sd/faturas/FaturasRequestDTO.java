package com.example.backend.sd.faturas;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturasRequestDTO
        (
                Integer pedido,
                String numeroFatura,
                LocalDate dataEmissao,
                BigDecimal valorTotal,
                LocalDate dataVencimento,
                String status
        ) {
}
