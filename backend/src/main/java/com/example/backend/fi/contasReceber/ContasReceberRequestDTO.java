package com.example.backend.fi.contasReceber;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.fi.centrosCusto.CentrosCusto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasReceberRequestDTO
        (
                Empresas empresaId,
                Parceiros clienteId,
                CentrosCusto centroCustoId,
                String numeroDocumento,
                String descricao,
                BigDecimal valorOriginal,
                BigDecimal valorPago,
                LocalDate dataEmissao,
                LocalDate dataVencimento,
                LocalDate dataPagamento,
                String status,
                String formaPagamento,
                LocalDateTime createdAt
        ) {
}
