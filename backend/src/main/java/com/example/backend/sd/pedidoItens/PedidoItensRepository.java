package com.example.backend.sd.pedidoItens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface PedidoItensRepository extends JpaRepository<PedidoItens, Integer> {
    boolean existsByPedidoId(Integer pedidoId);
    boolean existsByProdutoId(Integer produtoId);

    @Query("select coalesce(sum(pi.valorTotal), 0) from PedidoItens pi where pi.pedido.id = :pedidoId")
    BigDecimal sumValorTotalByPedidoId(Integer pedidoId);

    @Query("select coalesce(sum(pi.desconto), 0) from PedidoItens pi where pi.pedido.id = :pedidoId")
    BigDecimal sumDescontoByPedidoId(Integer pedidoId);
}
