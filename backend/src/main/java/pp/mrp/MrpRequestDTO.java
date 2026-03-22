package pp.mrp;

import core.produtos.Produtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MrpRequestDTO
        (
                Produtos produtoId,
                LocalDate periodo,
                BigDecimal demandaPrevista,
                BigDecimal estoqueAtual,
                BigDecimal estoqueSeguranca,
                BigDecimal necessidadeCompra,
                BigDecimal necessidadeProducao,
                LocalDate dataNecessidade,
                LocalDateTime createdAt
        ) {
}
