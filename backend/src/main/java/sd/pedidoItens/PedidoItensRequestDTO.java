package sd.pedidoItens;

import core.produtos.Produtos;
import sd.pedidos.Pedidos;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record PedidoItensRequestDTO
        (
                Pedidos pedidoId,
                Produtos produtoId,
                BigInteger quantidade,
                BigInteger valorUnitario,
                BigInteger valorTotal,
                BigInteger desconto,
                LocalDateTime createdAt
        ) {
}
