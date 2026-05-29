package com.example.backend.sd.pedidos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidosRepository extends JpaRepository<Pedidos, Integer> {
    boolean existsByNumeroPedido(String numeroPedido);
    boolean existsByNumeroPedidoAndIdNot(String numeroPedido, Integer id);
    boolean existsByClienteId(Integer clienteId);
}

