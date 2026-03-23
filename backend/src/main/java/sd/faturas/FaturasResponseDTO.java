package sd.faturas;


import sd.pedidos.Pedidos;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturasResponseDTO
        (
                Integer id,
                Pedidos pedidoId,
                String numeroFatura,
                LocalDate dataEmissao,
                BigDecimal valorTotal,
                LocalDate dataVencimento,
                String status,
                LocalDateTime createdAt
        )
    {
        public FaturasResponseDTO(Faturas faturas) {
            this
                    (
                            faturas.getId(),
                            faturas.getPedidoId(),
                            faturas.getNumeroFatura(),
                            faturas.getDataEmissao(),
                            faturas.getValorTotal(),
                            faturas.getDataVencimento(),
                            faturas.getStatus(),
                            faturas.getCreatedAt()
                    );
        }
}
