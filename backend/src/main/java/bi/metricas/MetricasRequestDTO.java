package bi.metricas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MetricasRequestDTO
        (
                String nome,
                String descricao,
                String categoria,
                String formula,
                String unidadeMedida,
                BigDecimal meta,
                LocalDateTime createdAt
        ) {
}
