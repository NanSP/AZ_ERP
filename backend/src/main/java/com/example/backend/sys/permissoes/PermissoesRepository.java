package com.example.backend.sys.permissoes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissoesRepository extends JpaRepository<Permissoes, Integer> {
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Integer id);
}
