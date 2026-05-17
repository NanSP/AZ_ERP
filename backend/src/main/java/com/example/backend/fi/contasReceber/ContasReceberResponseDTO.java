package com.example.backend.fi.contasReceber;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasReceberResponseDTO
        (
                Integer id,
                Integer empresaId,
                Integer clienteId,
                Integer centroCustoId,
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
        )
    {
        public ContasReceberResponseDTO(ContasReceber contasReceber) {
            this
                    (
                            contasReceber.getId(),
                            contasReceber.getEmpresa() != null ? contasReceber.getEmpresa().getId() : null,
                            contasReceber.getCliente() != null ? contasReceber.getCliente().getId() : null,
                            contasReceber.getCentroCusto() != null ? contasReceber.getCentroCusto().getId() : null,
                            contasReceber.getNumeroDocumento(),
                            contasReceber.getDescricao(),
                            contasReceber.getValorOriginal(),
                            contasReceber.getValorRecebido(),
                            contasReceber.getDataEmissao(),
                            contasReceber.getDataVencimento(),
                            contasReceber.getDataRecebimento(),
                            contasReceber.getStatus(),
                            contasReceber.getFormaPagamento(),
                            contasReceber.getCreatedAt()
                    );
        }
}
