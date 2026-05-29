package com.example.backend.sys.perfis;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfisRepository extends JpaRepository<Perfis, Integer> {
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Integer id);
}
