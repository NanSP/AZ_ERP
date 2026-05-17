package com.example.backend.sd.pedidoItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.sd.pedidos.Pedidos;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "pedido_itens", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PedidoItens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;
    @Column(name ="valor_unitario",precision = 15, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name ="valor_total",precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(precision = 15, scale = 2)
    private BigDecimal desconto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
