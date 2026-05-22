package com.example.backend.fi.contasPagar;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContasPagarRequestDTO
        (
                Integer empresa,
                Integer fornecedor,
                Integer centroCusto,
                String numeroDocumento,
                String descricao,
                BigDecimal valorOriginal,
                BigDecimal valorPago,
                LocalDate dataEmissao,
                LocalDate dataVencimento,
                LocalDate dataPagamento,
                String formaPagamento
        ) {
}
