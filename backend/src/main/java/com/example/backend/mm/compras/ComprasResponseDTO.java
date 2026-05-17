package com.example.backend.mm.compras;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ComprasResponseDTO
        (
                Integer id,
                Integer fornecedorId,
                LocalDate dataPedido,
                LocalDate dataPrevistaEntrega,
                LocalDate dataEntrega,
                BigDecimal valorTotal,
                String condicoesPagamento,
                String status,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public ComprasResponseDTO(Compras compras) {
            this
                    (
                            compras.getId(),
                            compras.getFornecedor() != null ? compras.getFornecedor().getId() : null,
                            compras.getDataPedido(),
                            compras.getDataPrevistaEntrega(),
                            compras.getDataEntrega(),
                            compras.getValorTotal(),
                            compras.getCondicoesPagamento(),
                            compras.getStatus(),
                            compras.getObservacoes(),
                            compras.getCreatedAt()
                    );
        }
}
