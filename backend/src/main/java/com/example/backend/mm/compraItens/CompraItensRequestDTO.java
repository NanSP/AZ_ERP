package com.example.backend.mm.compraItens;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraItensRequestDTO
        (
                Integer compras,
                Integer produtos,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal quantidadeRecebida,
                LocalDateTime createdAt
        ) {
}
