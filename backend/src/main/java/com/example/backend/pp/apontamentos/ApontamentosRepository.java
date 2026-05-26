package com.example.backend.pp.apontamentos;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApontamentosRepository extends JpaRepository<Apontamentos, Integer> {
    boolean existsByOpId(Integer opId);
    boolean existsByOpIdAndQuantidadeRefugoGreaterThan(Integer opId, BigDecimal quantidadeRefugo);
    @Query("""
            select coalesce(sum(a.quantidadeRefugo), 0)
            from Apontamentos a
            where a.op.id = :opId
            """)
    BigDecimal sumQuantidadeRefugoByOpId(@Param("opId") Integer opId);
}
