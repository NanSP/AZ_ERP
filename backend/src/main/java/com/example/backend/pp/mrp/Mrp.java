package com.example.backend.pp.mrp;

import com.example.backend.core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "mrp", schema = "producao")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mrp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    private LocalDate periodo;
    @Column(name = "demanda_prevista", precision = 15, scale = 4)
    private BigDecimal demandaPrevista;
    @Column(name = "estoque_atual", precision = 15, scale = 4)
    private BigDecimal estoqueAtual;
    @Column(name = "estoque_seguranca", precision = 15, scale = 4)
    private BigDecimal estoqueSeguranca;
    @Column(name = "necessidade_compra", precision = 15, scale = 4)
    private BigDecimal necessidadeCompra;
    @Column(name = "necessidade_producao", precision = 15, scale = 4)
    private BigDecimal necessidadeProducao;
    @Column(name = "data_necessidade")
    private LocalDate dataNecessidade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
