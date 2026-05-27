package com.example.backend.mm.compraItens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CompraItensRepository extends JpaRepository<CompraItens, Integer> {
    boolean existsByComprasId(Integer comprasId);
    boolean existsByComprasIdAndQuantidadeRecebidaGreaterThan(Integer comprasId, BigDecimal quantidadeRecebida);

    @Query("""
            select count(ci) > 0
            from CompraItens ci
            where ci.compras.id = :compraId
              and coalesce(ci.quantidadeRecebida, 0) < ci.quantidade
            """)
    boolean existsByComprasIdAndQuantidadeRecebidaLessThanQuantidade(Integer compraId);

    @Query("select coalesce(sum(ci.valorTotal), 0) from CompraItens ci where ci.compras.id = :compraId")
    BigDecimal sumValorTotalByCompraId(Integer compraId);

    @Query("select coalesce(sum(ci.quantidade), 0) from CompraItens ci where ci.compras.id = :compraId")
    BigDecimal sumQuantidadeByCompraId(Integer compraId);

    @Query("select coalesce(sum(ci.quantidadeRecebida), 0) from CompraItens ci where ci.compras.id = :compraId")
    BigDecimal sumQuantidadeRecebidaByCompraId(Integer compraId);
}
