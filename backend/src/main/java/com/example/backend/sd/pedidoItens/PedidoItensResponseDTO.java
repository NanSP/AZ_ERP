package com.example.backend.sd.pedidoItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoItensResponseDTO
        (
                Integer id,
                Pedidos pedidoId,
                Produtos produtoId,
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
                            pedidoItens.getPedidoId(),
                            pedidoItens.getProdutoId(),
                            pedidoItens.getQuantidade(),
                            pedidoItens.getValorUnitario(),
                            pedidoItens.getValorTotal(),
                            pedidoItens.getDesconto(),
                            pedidoItens.getCreatedAt()
                    );
        }
}
