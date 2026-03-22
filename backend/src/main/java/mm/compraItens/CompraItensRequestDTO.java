package mm.compraItens;

import core.produtos.Produtos;
import mm.compras.Compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraItensRequestDTO
        (
                Compras compraId,
                Produtos produtoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal quantidadeRecebida,
                LocalDateTime createdAt
        ) {
}
