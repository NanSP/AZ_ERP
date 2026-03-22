package fi.contasPagar;

import core.empresas.Empresas;
import core.parceiros.Parceiros;
import fi.centrosCusto.CentrosCusto;
import fi.planoContas.PlanoContas;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
