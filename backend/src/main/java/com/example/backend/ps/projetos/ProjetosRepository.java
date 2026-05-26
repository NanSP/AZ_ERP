package com.example.backend.ps.projetos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetosRepository extends JpaRepository<Projetos, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
