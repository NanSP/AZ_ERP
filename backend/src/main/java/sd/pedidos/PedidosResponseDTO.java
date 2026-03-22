package sd.pedidos;

import core.parceiros.Parceiros;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidosResponseDTO
        (
                Integer id,
                Parceiros clienteId,
                String numeroPedido,
                LocalDate dataPedido,
                LocalDate dataEntrega,
                BigDecimal valorTotal,
                BigDecimal descontoTotal,
                String condicoesPagamento,
                String status,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public PedidosResponseDTO(Pedidos pedidos) {
            this
                    (
                            pedidos.getId(),
                            pedidos.getClienteId(),
                            pedidos.getNumeroPedido(),
                            pedidos.getDataPedido(),
                            pedidos.getDataEntrega(),
                            pedidos.getValorTotal(),
                            pedidos.getDescontoTotal(),
                            pedidos.getCondicoesPagamento(),
                            pedidos.getStatus(),
                            pedidos.getObservacoes(),
                            pedidos.getCreatedAt()
                    );
        }
}
