package com.example.backend.bi.historicoMetricas;

import com.example.backend.bi.metricas.Metricas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoricoMetricasRequestDTO
        (
                Metricas metricaId,
                LocalDate periodo,
                BigDecimal valorApurado,
                LocalDateTime createdAt
        ) {
}
