package com.example.backend.pp.bom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BomResponseDTO
        (
                Integer id,
                Integer produtoPai,
                Integer componente,
                BigDecimal quantidade,
                String unidadeMedida,
                Integer nivel,
                BigDecimal tempoPreparacao,
                BigDecimal tempoProducao,
                Integer roteiro,
                LocalDateTime createdAt
        )
    {
        public BomResponseDTO(Bom bom) {
            this(
                    bom.getId(),
                    bom.getProdutoPai() != null ? bom.getProdutoPai().getId() : null,
                    bom.getComponente() != null ? bom.getComponente().getId() : null,
                    bom.getQuantidade(),
                    bom.getUnidadeMedida(),
                    bom.getNivel(),
                    bom.getTempoPreparacao(),
                    bom.getTempoProducao(),
                    bom.getRoteiro(),
                    bom.getCreatedAt()
            );
        }
}
