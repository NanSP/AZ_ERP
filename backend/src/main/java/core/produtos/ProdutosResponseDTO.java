package core.produtos;

import core.parceiros.Parceiros;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutosResponseDTO
        (
                Integer id,
                String codigo,
                String codigoBarras,
                String nome,
                String descricao,
                String tipoItem,
                String unidadeMedida,
                String ncm,
                String cest,
                BigDecimal pesoBruto,
                BigDecimal pesoLiquido,
                Integer origem,
                String situacao,
                LocalDateTime createdAt
        )
    {

public ProdutosResponseDTO(Produtos produtos) {
    this
            (
                    produtos.getId(),
                    produtos.getCodigo(),
                    produtos.getCodigoBarras(),
                    produtos.getNome(),
                    produtos.getDescricao(),
                    produtos.getTipoItem(),
                    produtos.getUnidadeMedida(),
                    produtos.getNcm(),
                    produtos.getCest(),
                    produtos.getPesoBruto(),
                    produtos.getPesoLiquido(),
                    produtos.getOrigem(),
                    produtos.getSituacao(),
                    produtos.getCreatedAt()
            );
    }
}
