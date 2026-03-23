package com.example.backend.qm.inspecoes;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InspecoesRequestDTO
        (
                String tipoInspecao,
                Produtos produtoId,
                String lote,
                BigDecimal quantidadeInspecionada,
                BigDecimal quantidadeAprovada,
                BigDecimal quantidadeReprovada,
                LocalDate dataInspecao,
                Colaboradores inspetorId,
                String resultado,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
