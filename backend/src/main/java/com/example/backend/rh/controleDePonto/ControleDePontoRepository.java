package com.example.backend.rh.controleDePonto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ControleDePontoRepository extends JpaRepository <ControleDePonto, Integer> {
    boolean existsByColaboradorIdAndData(Integer colaboradorId, LocalDate data);
    boolean existsByColaboradorIdAndDataAndIdNot(Integer colaboradorId, LocalDate data, Integer id);
}
