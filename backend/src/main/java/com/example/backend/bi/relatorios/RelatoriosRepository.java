package com.example.backend.bi.relatorios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatoriosRepository extends JpaRepository<Relatorios, Integer> {
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Integer id);
}
