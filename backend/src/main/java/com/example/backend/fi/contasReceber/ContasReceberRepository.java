package com.example.backend.fi.contasReceber;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContasReceberRepository extends JpaRepository<ContasReceber, Integer> {
    boolean existsByClienteId(Integer clienteId);
    boolean existsByEmpresaId(Integer empresaId);
}
