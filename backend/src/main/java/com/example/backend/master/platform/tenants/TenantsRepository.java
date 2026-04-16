package com.example.backend.master.platform.tenants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantsRepository extends JpaRepository<Tenants, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    Optional<Tenants> findByCodigoIgnoreCase(String codigo);

}
