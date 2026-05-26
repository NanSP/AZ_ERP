package com.example.backend.sd.faturas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FaturasRepository extends JpaRepository<Faturas,Integer> {
    boolean existsByPedidoId(Integer pedidoId);
    boolean existsByPedidoIdAndStatusNot(Integer pedidoId, String status);
    boolean existsByNumeroFatura(String numeroFatura);
    boolean existsByNumeroFaturaAndIdNot(String numeroFatura, Integer id);
}
