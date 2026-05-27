package com.example.backend.rh.controleDePonto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ControleDePontoRequestDTO
        (
                Integer colaborador,
                LocalDate data,
                LocalTime horaEntrada,
                LocalTime horaSaidaAlmoco,
                LocalTime horaRetornoAlmoco,
                LocalTime horaSaida
        ) {
}
