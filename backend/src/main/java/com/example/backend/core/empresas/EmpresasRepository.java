package com.example.backend.core.empresas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresasRepository extends JpaRepository<Empresas, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByCnpj(String cnpj);
    boolean existsByCnpjAndIdNot(String cnpj, Integer id);
}
