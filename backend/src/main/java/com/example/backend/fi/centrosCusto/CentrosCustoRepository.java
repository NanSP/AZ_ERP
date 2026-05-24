package com.example.backend.fi.centrosCusto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CentrosCustoRepository extends JpaRepository<CentrosCusto, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
