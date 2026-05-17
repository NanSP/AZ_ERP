package com.example.backend.am.bensPatrimoniais;

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
                Integer fornecedor,
                Integer responsavel,
                String status,
                LocalDateTime createdAt
        ) {
}
