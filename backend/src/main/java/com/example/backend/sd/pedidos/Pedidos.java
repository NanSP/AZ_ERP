package com.example.backend.sd.pedidos;

import com.example.backend.core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sd.pedidoItens.PedidoItens;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "pedidos", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedidos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros cliente;
    @Column(name = "numero_pedido")
    private String numeroPedido;
    @Column(name = "data_pedido")
    private LocalDate dataPedido;
    @Column(name = "data_entrega")
    private LocalDate dataEntrega;
    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "desconto_total", precision = 15, scale = 2)
    private BigDecimal descontoTotal;
    @Column(name = "condicoes_pagamento")
    private String condicoesPagamento;
    private String status;
    private String observacoes;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItens> itens = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
