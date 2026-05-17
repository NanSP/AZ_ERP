package com.example.backend.bi.historicoMetricas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoricoMetricasResponseDTO
        (
                Long id,
                Integer metrica,
                LocalDate periodo,
                BigDecimal valorApurado,
                LocalDateTime createdAt
        ) {
    public HistoricoMetricasResponseDTO(HistoricoMetricas historicoMetricas) {
        this
                (
                        historicoMetricas.getId(),
                        historicoMetricas.getMetrica() != null ? historicoMetricas.getMetrica().getId() : null,
                        historicoMetricas.getPeriodo(),
                        historicoMetricas.getValorApurado(),
                        historicoMetricas.getCreatedAt()
                );
    }
}
