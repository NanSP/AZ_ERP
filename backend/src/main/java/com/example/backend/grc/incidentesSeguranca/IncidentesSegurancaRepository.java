package com.example.backend.grc.incidentesSeguranca;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentesSegurancaRepository extends JpaRepository<IncidentesSeguranca, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
