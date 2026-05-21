package com.example.backend.mm.movimentacoes;

import java.math.BigDecimal;

public record MovimentacoesRequestDTO
        (
                Integer estoque,
                String tipoMovimento,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                String documentoReferencia,
                String motivo,
                Integer usuario
        ) {
}
