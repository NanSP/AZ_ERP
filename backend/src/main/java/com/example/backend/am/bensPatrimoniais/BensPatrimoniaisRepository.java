package com.example.backend.am.bensPatrimoniais;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BensPatrimoniaisRepository extends JpaRepository<BensPatrimoniais, Integer> {
    boolean existsByCodigoPatrimonio(String codigoPatrimonio);
    boolean existsByCodigoPatrimonioAndIdNot(String codigoPatrimonio, Integer id);
}
