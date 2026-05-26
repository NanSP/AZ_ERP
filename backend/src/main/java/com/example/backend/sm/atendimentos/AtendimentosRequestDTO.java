package com.example.backend.sm.atendimentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentosRequestDTO
        (
                Integer os,
                Integer tecnico,
                LocalDateTime dataHora,
                String descricao,
                BigDecimal horasGastas,
                Map<String, Object> materiaisUtilizados
        ) {
}
