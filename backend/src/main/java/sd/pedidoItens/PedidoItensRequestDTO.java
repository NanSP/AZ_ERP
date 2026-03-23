package sd.pedidoItens;

import core.produtos.Produtos;
import sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoItensRequestDTO
        (
                Pedidos pedidoId,
                Produtos produtoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal desconto,
                LocalDateTime createdAt
        ) {
}
