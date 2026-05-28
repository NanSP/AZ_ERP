package com.example.backend.grc.controles;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ControlesRepository extends JpaRepository<Controles, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
