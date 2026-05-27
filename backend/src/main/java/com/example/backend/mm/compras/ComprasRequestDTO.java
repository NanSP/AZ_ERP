package com.example.backend.mm.compras;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprasRequestDTO
        (
                Integer fornecedor,
                LocalDate dataPedido,
                LocalDate dataPrevistaEntrega,
                LocalDate dataEntrega,
                BigDecimal valorTotal,
                String condicoesPagamento,
                String status,
                String observacoes
        ) {
}
