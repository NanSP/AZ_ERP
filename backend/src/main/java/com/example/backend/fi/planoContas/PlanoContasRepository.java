package com.example.backend.fi.planoContas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoContasRepository extends JpaRepository<PlanoContas, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
