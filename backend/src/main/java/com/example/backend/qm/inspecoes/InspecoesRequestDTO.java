package com.example.backend.qm.inspecoes;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InspecoesRequestDTO
        (
                String tipoInspecao,
                Integer produto,
                String lote,
                BigDecimal quantidadeInspecionada,
                BigDecimal quantidadeAprovada,
                BigDecimal quantidadeReprovada,
                LocalDate dataInspecao,
                Integer inspetor,
                String resultado,
                String observacoes
        ) {
}
