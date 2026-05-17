package com.example.backend.mm.estoques;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EstoquesResponseDTO
        (
                Integer id,
                Integer produto,
                Integer empresa,
                String localizacao,
                String lote,
                BigDecimal quantidade,
                BigDecimal quantidadeMinima,
                BigDecimal quantidadeMaxima,
                BigDecimal valorUnitario,
                LocalDate dataValidade,
                LocalDateTime createdAt
        )
    {
        public EstoquesResponseDTO(Estoques estoques) {
            this
                    (
                            estoques.getId(),
                            estoques.getProduto() != null ? estoques.getProduto().getId() : null,
                            estoques.getEmpresa() != null ? estoques.getEmpresa().getId() : null,
                            estoques.getLocalizacao(),
                            estoques.getLote(),
                            estoques.getQuantidade(),
                            estoques.getQuantidadeMinima(),
                            estoques.getQuantidadeMaxima(),
                            estoques.getValorUnitario(),
                            estoques.getDataValidade(),
                            estoques.getCreatedAt()
                    );
        }
}
