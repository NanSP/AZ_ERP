package com.example.backend.fi.contasPagar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasPagarResponseDTO
        (
                Integer id,
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
                String status,
                String formaPagamento,
                LocalDateTime createdAt
        )
    {
        public ContasPagarResponseDTO(ContasPagar contasPagar) {
            this
                    (
                            contasPagar.getId(),
                            contasPagar.getEmpresa() != null ? contasPagar.getEmpresa().getId() : null,
                            contasPagar.getFornecedor() != null ? contasPagar.getFornecedor().getId() : null,
                            contasPagar.getCentroCusto() != null ? contasPagar.getCentroCusto().getId() : null,
                            contasPagar.getNumeroDocumento(),
                            contasPagar.getDescricao(),
                            contasPagar.getValorOriginal(),
                            contasPagar.getValorPago(),
                            contasPagar.getDataEmissao(),
                            contasPagar.getDataVencimento(),
                            contasPagar.getDataPagamento(),
                            contasPagar.getStatus(),
                            contasPagar.getFormaPagamento(),
                            contasPagar.getCreatedAt()
                    );
        }
}
