package com.example.backend.sd.oportunidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OportunidadesRequestDTO
        (
                Integer cliente,
                String titulo,
                String descricao,
                BigDecimal valorEstimado,
                Integer probabilidade,
                String estagio,
                LocalDate dataPrevistaFechamento,
                String motivoPerda,
                Integer responsavel,
                LocalDateTime createdAt
        ) {
}
