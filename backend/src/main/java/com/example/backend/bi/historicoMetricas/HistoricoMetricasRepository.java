package com.example.backend.bi.historicoMetricas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HistoricoMetricasRepository extends JpaRepository<HistoricoMetricas, Long> {
    boolean existsByMetricaId(Integer metricaId);
    boolean existsByMetricaIdAndPeriodo(Integer metricaId, LocalDate periodo);
    boolean existsByMetricaIdAndPeriodoAndIdNot(Integer metricaId, LocalDate periodo, Long id);
}
