package com.example.backend.sd.faturas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturasResponseDTO
        (
                Integer id,
                Integer pedido,
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
                            faturas.getPedido() != null ? faturas.getPedido().getId() : null,
                            faturas.getNumeroFatura(),
                            faturas.getDataEmissao(),
                            faturas.getValorTotal(),
                            faturas.getDataVencimento(),
                            faturas.getStatus(),
                            faturas.getCreatedAt()
                    );
        }
}
