package com.example.backend.sd.pedidos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidosRequestDTO
        (Integer cliente,
         String numeroPedido,
         LocalDate dataPedido,
         LocalDate dataEntrega,
         BigDecimal valorTotal,
         BigDecimal descontoTotal,
         String condicoesPagamento,
         String status,
         String observacoes,
         LocalDateTime createdAt) {
}
