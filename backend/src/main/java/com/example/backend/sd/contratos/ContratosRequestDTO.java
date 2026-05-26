package com.example.backend.sd.contratos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratosRequestDTO
        (
                Integer cliente,
                String numeroContrato,
                String objeto,
                BigDecimal valorTotal,
                LocalDate dataInicio,
                LocalDate dataFim,
                String status
        ) {
}
