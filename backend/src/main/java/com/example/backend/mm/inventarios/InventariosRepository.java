package com.example.backend.mm.inventarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventariosRepository extends JpaRepository<Inventarios, Integer> {
    boolean existsByTipoInventarioAndStatus(String tipoInventario, String status);
    boolean existsByTipoInventarioAndStatusAndIdNot(String tipoInventario, String status, Integer id);
    boolean existsByStatusAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(String status, LocalDate dataFim, LocalDate dataInicio);
    boolean existsByStatusAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(String status, LocalDate dataFim, LocalDate dataInicio, Integer id);
}
