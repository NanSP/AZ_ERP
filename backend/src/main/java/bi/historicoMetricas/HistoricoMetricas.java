package bi.historicoMetricas;

import bi.metricas.Metricas;
import core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "historico_metricas", schema = "bi")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class HistoricoMetricas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "metrica_id")
    private Metricas metricaId;

    private LocalDate periodo;
    @Column(name = "valor_apurado", precision = 10, scale = 2)
    private BigDecimal valorApurado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public HistoricoMetricas(HistoricoMetricasRequestDTO data) {
        this.metricaId = data.metricaId();
        this.periodo = data.periodo();
        this.valorApurado = data.valorApurado();
        this.createdAt = data.createdAt();
    }
}
