package mm.compras;

import core.parceiros.Parceiros;
import mm.compraItens.CompraItens;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ComprasRequestDTO
        (
                List<CompraItens> itens,
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
