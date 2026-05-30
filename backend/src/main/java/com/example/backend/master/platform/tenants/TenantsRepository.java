package com.example.backend.master.platform.tenants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantsRepository extends JpaRepository<Tenants, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Long id);
    Optional<Tenants> findByCodigo(String codigo);
}
