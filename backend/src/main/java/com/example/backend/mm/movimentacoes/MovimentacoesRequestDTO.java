package com.example.backend.mm.movimentacoes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacoesRequestDTO
        (
                Integer estoque,
                String tipoMovimento,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                String documentoReferencia,
                String motivo,
                Integer usuario,
                LocalDateTime createdAt
        ) {
}
