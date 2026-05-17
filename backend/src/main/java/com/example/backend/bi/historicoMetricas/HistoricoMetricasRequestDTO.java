package com.example.backend.bi.historicoMetricas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoricoMetricasRequestDTO
        (
                Integer metrica,
                LocalDate periodo,
                BigDecimal valorApurado,
                LocalDateTime createdAt
        ) {
}
