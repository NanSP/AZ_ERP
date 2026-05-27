package com.example.backend.rh.beneficios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiosRepository extends JpaRepository<Beneficios, Integer> {
    boolean existsByColaboradorId(Integer colaboradorId);
    boolean existsByColaboradorIdAndTipoBeneficioAndAtivoTrue(Integer colaboradorId, String tipoBeneficio);
    boolean existsByColaboradorIdAndTipoBeneficioAndAtivoTrueAndIdNot(Integer colaboradorId, String tipoBeneficio, Integer id);
}
