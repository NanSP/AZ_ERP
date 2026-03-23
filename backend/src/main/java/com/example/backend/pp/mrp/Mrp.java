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
    private Produtos produtoId;

    private LocalDate periodo;
    @Column(name = "demanda_prevista", precision = 10, scale = 4)
    private BigDecimal demandaPrevista;
    @Column(name = "estoque_atual", precision = 10, scale = 4)
    private BigDecimal estoqueAtual;
    @Column(name = "estoque_seguranca", precision = 10, scale = 4)
    private BigDecimal estoqueSeguranca;
    @Column(name = "necessidade_compra", precision = 10, scale = 4)
    private BigDecimal necessidadeCompra;
    @Column(name = "necessidade_producao", precision = 10, scale = 4)
    private BigDecimal necessidadeProducao;
    @Column(name = "data_necessidade")
    private LocalDate dataNecessidade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Mrp(MrpRequestDTO data) {
        this.produtoId = data.produtoId();
        this.demandaPrevista = data.demandaPrevista();
        this.periodo = data.periodo();
        this.estoqueAtual = data.estoqueAtual();
        this.estoqueSeguranca = data.estoqueSeguranca();
        this.necessidadeCompra = data.necessidadeCompra();
        this.dataNecessidade = data.dataNecessidade();
        this.necessidadeProducao = data.necessidadeProducao();
        this.createdAt = data.createdAt();
    }
}
