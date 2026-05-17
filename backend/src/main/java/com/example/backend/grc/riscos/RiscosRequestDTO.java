package com.example.backend.grc.riscos;

import java.time.LocalDateTime;

public record RiscosRequestDTO
        (
                String codigo,
                String titulo,
                String descricao,
                String categoria,
                Integer probabilidade,
                Integer impacto,
                String nivelRisco,
                Integer responsavel,
                String planoMitigacao,
                LocalDateTime createdAt
        ) {
}
