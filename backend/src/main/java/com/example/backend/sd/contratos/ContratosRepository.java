package com.example.backend.sd.contratos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratosRepository extends JpaRepository<Contratos, Integer> {
    boolean existsByNumeroContrato(String numeroContrato);
    boolean existsByNumeroContratoAndIdNot(String numeroContrato, Integer id);
    boolean existsByClienteId(Integer clienteId);
}
