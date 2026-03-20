package rh.controleDePonto;

import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ControleDePontoRequestDTO
        (
                Colaboradores colaboradorId,
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
