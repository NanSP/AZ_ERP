package com.example.backend.mm.materiais;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaisRepository extends JpaRepository<Materiais, Integer> {
    boolean existsByProdutoId(Integer produtoId);
    boolean existsByProdutoIdAndIdNot(Integer produtoId, Integer id);
}
