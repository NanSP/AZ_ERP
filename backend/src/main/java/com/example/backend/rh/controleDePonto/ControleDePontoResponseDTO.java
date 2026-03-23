package com.example.backend.rh.controleDePonto;

import com.example.backend.rh.colaboradores.Colaboradores;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ControleDePontoResponseDTO
        (
                Integer id,
                Colaboradores colaborador,
                LocalDate data,
                LocalTime horaEntrada,
                LocalTime horaSaidaAlmoco,
                LocalTime horaRetornoAlmoco,
                LocalTime horaSaida,
                BigDecimal horasTrabalhadas,
                BigDecimal horasExtras,
                Integer atrasos,
                LocalDateTime createdAt
        )
    {
        public ControleDePontoResponseDTO(ControleDePonto controleDePonto){
            this
                    (
                            controleDePonto.getId(),
                            controleDePonto.getColaboradorId(),
                            controleDePonto.getData(),
                            controleDePonto.getHoraEntrada(),
                            controleDePonto.getHoraSaidaAlmoco(),
                            controleDePonto.getHoraRetornoAlmoco(),
                            controleDePonto.getHoraSaida(),
                            controleDePonto.getHorasTrabalhadas(),
                            controleDePonto.getHorasExtras(),
                            controleDePonto.getAtrasos(),
                            controleDePonto.getCreatedAt()
                    );
        }
}
