package com.example.backend.sd.pedidos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.sd.pedidoItens.PedidoItens;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                List<PedidoItens> itens,
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
                            pedidos.getItens(),
                            pedidos.getCreatedAt()
                    );
        }
}
