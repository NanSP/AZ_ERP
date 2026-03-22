package mm.compras;

import core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "compras", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Compras {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Parceiros fornecedorId;
    @Column(name = "data_pedido")
    private LocalDate dataPedido;
    @Column(name = "data_prevista_entrega")
    private LocalDate dataPrevistaEntrega;
    @Column(name = "data_entrega")
    private LocalDate dataEntrega;
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "condicoes_pagamento")
    private String condicoesPagamento;
    private String status;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public Compras(ComprasRequestDTO data) {
        this.fornecedorId = data.fornecedorId();
        this.dataPedido = data.dataPedido();
        this.dataPrevistaEntrega = data.dataPrevistaEntrega();
        this.dataEntrega = data.dataEntrega();
        this.valorTotal = data.valorTotal();
        this.condicoesPagamento = data.condicoesPagamento();
        this.observacoes = data.observacoes();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
