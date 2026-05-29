package com.example.backend.core.parceiros;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParceirosRepository extends JpaRepository<Parceiros, Integer> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Integer id);
}
