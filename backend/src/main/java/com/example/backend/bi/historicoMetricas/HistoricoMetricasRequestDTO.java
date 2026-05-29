package com.example.backend.bi.historicoMetricas;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricoMetricasRequestDTO
        (
                Integer metrica,
                LocalDate periodo,
                BigDecimal valorApurado
        ) {
}
