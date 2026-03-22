package fi.contasReceber;

import core.empresas.Empresas;
import core.parceiros.Parceiros;
import fi.centrosCusto.CentrosCusto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContasReceberResponseDTO
        (
                Integer id,
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
        )
    {
        public ContasReceberResponseDTO(ContasReceber contasReceber) {
            this
                    (
                            contasReceber.getId(),
                            contasReceber.getEmpresaId(),
                            contasReceber.getClienteId(),
                            contasReceber.getCentroCustoId(),
                            contasReceber.getNumeroDocumento(),
                            contasReceber.getDescricao(),
                            contasReceber.getValorOriginal(),
                            contasReceber.getValorPago(),
                            contasReceber.getDataEmissao(),
                            contasReceber.getDataVencimento(),
                            contasReceber.getDataPagamento(),
                            contasReceber.getStatus(),
                            contasReceber.getFormaPagamento(),
                            contasReceber.getCreatedAt()
                    );
        }
}
