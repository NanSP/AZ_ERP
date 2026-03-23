package sd.pedidos;

import core.parceiros.Parceiros;
import sd.pedidoItens.PedidoItens;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
         List<PedidoItens> itens,
         LocalDateTime createdAt) {
}
