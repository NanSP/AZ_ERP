package com.example.backend.mm.compraItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.mm.compras.Compras;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "compra_itens", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompraItens {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compras compras;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtos;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "valor_unitario", precision = 15, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "quantidade_recebida", precision = 15, scale = 4)
    private BigDecimal quantidadeRecebida;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
