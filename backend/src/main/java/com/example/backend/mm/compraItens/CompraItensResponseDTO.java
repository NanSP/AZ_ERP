package com.example.backend.mm.compraItens;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraItensResponseDTO
        (
                Integer id,
                Integer compras,
                Integer produtos,
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
                            compraItens.getCompras() != null ? compraItens.getCompras().getId() : null,
                            compraItens.getProdutos() != null ? compraItens.getProdutos().getId() : null,
                            compraItens.getQuantidade(),
                            compraItens.getValorUnitario(),
                            compraItens.getValorTotal(),
                            compraItens.getQuantidadeRecebida(),
                            compraItens.getCreatedAt()
                    );
        }
}
