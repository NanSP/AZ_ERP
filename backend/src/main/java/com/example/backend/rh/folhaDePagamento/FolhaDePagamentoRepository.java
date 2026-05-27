package com.example.backend.rh.folhaDePagamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FolhaDePagamentoRepository extends JpaRepository<FolhaDePagamento, Integer> {
    boolean existsByColaboradorId(Integer colaboradorId);
    boolean existsByColaboradorIdAndCompetencia(Integer colaboradorId, LocalDate competencia);
    boolean existsByColaboradorIdAndCompetenciaAndIdNot(Integer colaboradorId, LocalDate competencia, Integer id);

}
