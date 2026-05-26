package com.example.backend.sd.clientes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientesRepository extends JpaRepository<Clientes, Integer> {
    boolean existsByParceiroId(Integer parceiroId);
    boolean existsByParceiroIdAndIdNot(Integer parceiroId, Integer id);
}
