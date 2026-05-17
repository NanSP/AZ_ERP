package com.example.backend.ps.recursosAlocados;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecursosAlocadosRequestDTO
        (
                Integer projeto,
                Integer tarefa,
                String tipoRecurso,
                Integer recursoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                LocalDate dataAlocacao,
                LocalDateTime createdAt
        ) {
}
