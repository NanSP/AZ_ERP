package mm.estoques;

import core.empresas.Empresas;
import core.produtos.Produtos;
import mm.movimentacoes.Movimentacoes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EstoquesRequestDTO
        (
                Produtos produtoId,
                Empresas empresaId,
                String localizacao,
                String lote,
                BigDecimal quantidade,
                BigDecimal quantidadeMinima,
                BigDecimal quantidadeMaxima,
                BigDecimal valorUnitario,
                LocalDate dataValidade,
                List<Movimentacoes> movimentacoes,
                LocalDateTime createdAt
        ) {
}
