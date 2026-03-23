package com.example.backend.fi.contasPagar;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.fi.centrosCusto.CentrosCusto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasPagarResponseDTO
        (
                Integer id,
                Empresas empresaId,
                Parceiros fornecedorId,
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
        )
    {
        public ContasPagarResponseDTO(ContasPagar contasPagar) {
            this
                    (
                            contasPagar.getId(),
                            contasPagar.getEmpresaId(),
                            contasPagar.getFornecedorId(),
                            contasPagar.getCentroCustoId(),
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
