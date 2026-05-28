package com.example.backend.grc.riscos;


public record RiscosRequestDTO
        (
                String codigo,
                String titulo,
                String descricao,
                String categoria,
                Integer probabilidade,
                Integer impacto,
                Integer responsavel,
                String planoMitigacao
        ) {
}
