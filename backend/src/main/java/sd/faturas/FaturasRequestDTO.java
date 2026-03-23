package sd.faturas;

import sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturasRequestDTO
        (
                Pedidos pedidoId,
                String numeroFatura,
                LocalDate dataEmissao,
                BigDecimal valorTotal,
                LocalDate dataVencimento,
                String status,
                LocalDateTime createdAt
        ) {
}
