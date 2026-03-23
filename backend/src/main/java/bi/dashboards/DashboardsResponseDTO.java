package bi.dashboards;

import jakarta.persistence.Column;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

public record DashboardsResponseDTO
        (
                Integer id,
                String nome,
                String descricao,
                Map<String, Object> layout,
                Map<String, Object> configuracoes,
                LocalDateTime createdAt
        ) {
    public DashboardsResponseDTO(Dashboards dashboards) {
        this
                (
                        dashboards.getId(),
                        dashboards.getNome(),
                        dashboards.getDescricao(),
                        dashboards.getLayout(),
                        dashboards.getConfiguracoes(),
                        dashboards.getCreatedAt()
                );
    }
}
