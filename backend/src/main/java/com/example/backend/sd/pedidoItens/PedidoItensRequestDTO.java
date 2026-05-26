package com.example.backend.sd.pedidoItens;

import java.math.BigDecimal;

public record PedidoItensRequestDTO
        (
                Integer pedido,
                Integer produto,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal desconto
        ) {
}
