package am.bensPatrimoniais;

import core.parceiros.Parceiros;
import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BensPatrimoniaisRequestDTO
        (
                String codigoPatrimonio,
                String nome,
                String descricao,
                String tipoAtivo,
                String localizacao,
                LocalDate dataAquisicao,
                BigDecimal valorAquisicao,
                BigDecimal valorAtual,
                Integer vidaUtilAnos,
                BigDecimal taxaDepreciacao,
                LocalDate dataDepreciacao,
                Parceiros fornecedorId,
                Colaboradores responsavelId,
                String status,
                LocalDateTime createdAt
        ) {
}
