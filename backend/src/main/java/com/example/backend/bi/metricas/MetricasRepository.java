package com.example.backend.bi.metricas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricasRepository extends JpaRepository<Metricas, Integer> {
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Integer id);
}
