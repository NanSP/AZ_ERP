package com.example.backend.ps.recursosAlocados;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecursosAlocadosRequestDTO
        (
                Integer projeto,
                Integer tarefa,
                String tipoRecurso,
                Integer recursoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                LocalDate dataAlocacao
        ) {
}
