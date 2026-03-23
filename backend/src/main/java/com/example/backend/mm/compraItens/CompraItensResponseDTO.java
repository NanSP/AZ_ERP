package com.example.backend.mm.compraItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.mm.compras.Compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraItensResponseDTO
        (
                Integer id,
                Compras compraId,
                Produtos produtoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                BigDecimal quantidadeRecebida,
                LocalDateTime createdAt
        )
    {
        public CompraItensResponseDTO(CompraItens compraItens) {
            this
                    (
                            compraItens.getId(),
                            compraItens.getCompraId(),
                            compraItens.getProdutoId(),
                            compraItens.getQuantidade(),
                            compraItens.getValorUnitario(),
                            compraItens.getValorTotal(),
                            compraItens.getQuantidadeRecebida(),
                            compraItens.getCreatedAt()
                    );
        }
}
