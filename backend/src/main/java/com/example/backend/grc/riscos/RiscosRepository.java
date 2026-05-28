package com.example.backend.grc.riscos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RiscosRepository extends JpaRepository<Riscos, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
