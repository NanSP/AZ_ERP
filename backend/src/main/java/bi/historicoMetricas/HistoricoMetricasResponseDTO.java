package bi.historicoMetricas;

import bi.metricas.Metricas;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoricoMetricasResponseDTO
        (
                Integer id,
                Metricas metricaId,
                LocalDate periodo,
                BigDecimal valorApurado,
                LocalDateTime createdAt
        ) {
    public HistoricoMetricasResponseDTO(HistoricoMetricas historicoMetricas) {
        this
                (
                        historicoMetricas.getId(),
                        historicoMetricas.getMetricaId(),
                        historicoMetricas.getPeriodo(),
                        historicoMetricas.getValorApurado(),
                        historicoMetricas.getCreatedAt()
                );
    }
}
