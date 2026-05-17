package com.example.backend.bi.historicoMetricas;

import com.example.backend.bi.metricas.Metricas;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "metrica_id")
    private Metricas metrica;

    private LocalDate periodo;
    @Column(name = "valor_apurado", precision = 15, scale = 2)
    private BigDecimal valorApurado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
