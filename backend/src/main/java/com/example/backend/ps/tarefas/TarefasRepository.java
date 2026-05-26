package com.example.backend.ps.tarefas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefasRepository extends JpaRepository<Tarefas, Integer> {
    long countByProjetoId(Integer projetoId);
    boolean existsByProjetoIdAndStatus(Integer projetoId, String status);
    boolean existsByProjetoIdAndStatusNot(Integer projetoId, String status);
}
