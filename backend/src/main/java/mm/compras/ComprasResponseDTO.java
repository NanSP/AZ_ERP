package mm.compras;

import core.parceiros.Parceiros;
import mm.movimentacoes.Movimentacoes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ComprasResponseDTO
        (
                Integer id,
                Parceiros fornecedorId,
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
                            compras.getFornecedorId(),
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
