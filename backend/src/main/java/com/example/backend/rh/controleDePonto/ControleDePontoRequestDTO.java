package com.example.backend.rh.controleDePonto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ControleDePontoRequestDTO
        (
                Integer colaborador,
                LocalDate data,
                LocalTime horaEntrada,
                LocalTime horaSaidaAlmoco,
                LocalTime horaRetornoAlmoco,
                LocalTime horaSaida,
                BigDecimal horasTrabalhadas,
                BigDecimal horasExtras,
                Integer atrasos,
                LocalDateTime createdAt
        ) {
}
