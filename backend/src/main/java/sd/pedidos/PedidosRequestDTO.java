package sd.pedidos;

import core.parceiros.Parceiros;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidosRequestDTO
        (Parceiros clienteId,
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
