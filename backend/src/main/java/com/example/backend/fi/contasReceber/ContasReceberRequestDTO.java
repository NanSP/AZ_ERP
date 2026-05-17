package com.example.backend.fi.contasReceber;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasReceberRequestDTO
        (
                Integer empresa,
                Integer cliente,
                Integer centroCusto,
                String numeroDocumento,
                String descricao,
                BigDecimal valorOriginal,
                BigDecimal valorRecebido,
                LocalDate dataEmissao,
                LocalDate dataVencimento,
                LocalDate dataRecebimento,
                String status,
                String formaPagamento,
                LocalDateTime createdAt
        ) {
}
