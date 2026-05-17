package com.example.backend.sd.pedidoItens;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoItensResponseDTO
        (
                Integer id,
                Integer pedido,
                Integer produto,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal desconto,
                LocalDateTime createdAt
        )
    {
        public PedidoItensResponseDTO(PedidoItens pedidoItens) {
            this
                    (
                            pedidoItens.getId(),
                            pedidoItens.getPedido() != null ? pedidoItens.getPedido().getId() : null,
                            pedidoItens.getProduto() != null ? pedidoItens.getProduto().getId() : null,
                            pedidoItens.getQuantidade(),
                            pedidoItens.getValorUnitario(),
                            pedidoItens.getValorTotal(),
                            pedidoItens.getDesconto(),
                            pedidoItens.getCreatedAt()
                    );
        }
}
