package pp.mrp;

import core.produtos.Produtos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MrpResponseDTO
        (
                Integer id,
                Produtos produtoId,
                LocalDate periodo,
                BigDecimal demandaPrevista,
                BigDecimal estoqueAtual,
                BigDecimal estoqueSeguranca,
                BigDecimal necessidadeCompra,
                BigDecimal necessidadeProducao,
                LocalDate dataNecessidade,
                LocalDateTime createdAt
        )
    {
        public MrpResponseDTO(Mrp mrp) {
            this(
                    mrp.getId(),
                    mrp.getProdutoId(),
                    mrp.getPeriodo(),
                    mrp.getDemandaPrevista(),
                    mrp.getEstoqueAtual(),
                    mrp.getEstoqueSeguranca(),
                    mrp.getNecessidadeCompra(),
                    mrp.getNecessidadeProducao(),
                    mrp.getDataNecessidade(),
                    mrp.getCreatedAt()
            );
        }
}
