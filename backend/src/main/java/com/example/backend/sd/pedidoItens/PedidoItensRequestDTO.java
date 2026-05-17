package com.example.backend.sd.pedidoItens;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoItensRequestDTO
        (
                Integer pedido,
                Integer produto,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal desconto,
                LocalDateTime createdAt
        ) {
}
