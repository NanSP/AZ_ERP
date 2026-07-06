package com.example.backend.grc.relatoriosImpacto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatoriosImpactoRepository extends JpaRepository<RelatoriosImpacto, Integer> {
    boolean existsByTituloIgnoreCase(String titulo);
    boolean existsByTituloIgnoreCaseAndIdNot(String titulo, Integer id);
}
