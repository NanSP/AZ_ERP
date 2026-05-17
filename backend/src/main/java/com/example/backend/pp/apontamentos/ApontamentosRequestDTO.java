package com.example.backend.pp.apontamentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApontamentosRequestDTO
        (
                Integer op,
                Integer maquinaId,
                Integer operador,
                LocalDateTime dataHoraInicio,
                LocalDateTime dataHoraFim,
                BigDecimal quantidadeProduzida,
                BigDecimal quantidadeRefugo,
                BigDecimal tempoParado,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
