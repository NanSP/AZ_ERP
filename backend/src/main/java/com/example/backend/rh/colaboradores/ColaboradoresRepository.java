package com.example.backend.rh.colaboradores;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradoresRepository extends JpaRepository<Colaboradores, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Integer id);
}
