package pp.bom;

import core.produtos.Produtos;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BomResponseDTO
        (
                Integer id,
                Produtos produtoPaiId,
                Produtos componenteId,
                BigDecimal quantidade,
                String unidadeMedida,
                Integer nivel,
                BigDecimal tempoPreparacao,
                BigDecimal tempoProducao,
                Integer roteiroId,
                LocalDateTime createdAt
        )
    {
        public BomResponseDTO(Bom bom) {
            this(
                    bom.getId(),
                    bom.getProdutoPaiId(),
                    bom.getComponenteId(),
                    bom.getQuantidade(),
                    bom.getUnidadeMedida(),
                    bom.getNivel(),
                    bom.getTempoPreparacao(),
                    bom.getTempoProducao(),
                    bom.getRoteiroId(),
                    bom.getCreatedAt()
            );
        }
}
