package com.example.backend.rh.controleDePonto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ControleDePontoRepository extends JpaRepository <ControleDePonto, Integer> {
    boolean existsByColaboradorId(Integer colaboradorId);
    boolean existsByColaboradorIdAndData(Integer colaboradorId, LocalDate data);
    boolean existsByColaboradorIdAndDataAndIdNot(Integer colaboradorId, LocalDate data, Integer id);

    @Query("""
            select coalesce(sum(cp.horasTrabalhadas), 0)
            from ControleDePonto cp
            where cp.colaborador.id = :colaboradorId
              and cp.data >= :dataInicio
              and cp.data <= :dataFim
            """)
    BigDecimal sumHorasTrabalhadasByColaboradorIdAndPeriodo(Integer colaboradorId, LocalDate dataInicio, LocalDate dataFim);

    @Query("""
            select coalesce(sum(cp.horasExtras), 0)
            from ControleDePonto cp
            where cp.colaborador.id = :colaboradorId
              and cp.data >= :dataInicio
              and cp.data <= :dataFim
            """)
    BigDecimal sumHorasExtrasByColaboradorIdAndPeriodo(Integer colaboradorId, LocalDate dataInicio, LocalDate dataFim);
}
