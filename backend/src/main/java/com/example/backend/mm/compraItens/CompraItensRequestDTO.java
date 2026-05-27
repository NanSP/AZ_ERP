package com.example.backend.mm.compraItens;

import java.math.BigDecimal;

public record CompraItensRequestDTO
        (
                Integer compras,
                Integer produtos,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal quantidadeRecebida
        ) {
}
