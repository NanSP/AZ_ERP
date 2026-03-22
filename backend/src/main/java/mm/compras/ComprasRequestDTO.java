package mm.compras;

import core.parceiros.Parceiros;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ComprasRequestDTO
        (
                Parceiros fornecedorId,
                LocalDate dataPedido,
                LocalDate dataPrevistaEntrega,
                LocalDate dataEntrega,
                BigDecimal valorTotal,
                String condicoesPagamento,
                String status,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
