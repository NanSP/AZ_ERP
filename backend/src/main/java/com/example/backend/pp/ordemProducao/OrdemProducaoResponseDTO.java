package com.example.backend.pp.ordemProducao;

import com.example.backend.core.produtos.Produtos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdemProducaoResponseDTO
        (
                Integer id,
                String numeroOp,
                Produtos produtoId,
                BigDecimal quantidaePlanejada,
                BigDecimal quantidadeProduzida,
                LocalDate dataEmissao,
                LocalDate dataInicio,
                LocalDate dataFim,
                LocalDate dataPrevista,
                String status,
                Integer prioridade,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public OrdemProducaoResponseDTO(OrdemProducao ordemProducao) {
            this(
                    ordemProducao.getId(),
                    ordemProducao.getNumeroOp(),
                    ordemProducao.getProdutoId(),
                    ordemProducao.getQuantidadePlanejada(),
                    ordemProducao.getQuantidadeProduzida(),
                    ordemProducao.getDataEmissao(),
                    ordemProducao.getDataInicio(),
                    ordemProducao.getDataFim(),
                    ordemProducao.getDataPrevista(),
                    ordemProducao.getStatus(),
                    ordemProducao.getPrioridade(),
                    ordemProducao.getObservacoes(),
                    ordemProducao.getCreatedAt()
            );
        }
}
