package com.example.backend.mm.compraItens;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraItensRepository extends JpaRepository<CompraItens, Integer> {
    boolean existsByComprasId(Integer comprasId);
}
