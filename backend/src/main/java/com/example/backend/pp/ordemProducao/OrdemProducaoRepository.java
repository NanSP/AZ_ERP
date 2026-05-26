package com.example.backend.pp.ordemProducao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Integer> {
    boolean existsByNumeroOp(String numeroOp);
    boolean existsByNumeroOpAndIdNot(String numeroOp, Integer id);
}
