package com.example.backend.pp.bom;

import com.example.backend.core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "bom", schema = "producao")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_pai_id")
    private Produtos produtoPaiId;

    @ManyToOne
    @JoinColumn(name = "componente_id")
    private Produtos componenteId;
    @Column(precision = 10, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "unidade_medida")
    private String unidadeMedida;
    private Integer nivel;
    @Column(name = "tempo_preparacao",precision = 10, scale = 4)
    private BigDecimal tempoPreparacao;
    @Column(name = "tempo_producao",precision = 10, scale = 4)
    private BigDecimal tempoProducao;
    @Column(name = "roteiro_id")
    private Integer roteiroId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Bom(BomRequestDTO data) {
        this.produtoPaiId = data.produtoPaiId();
        this.componenteId = data.componenteId();
        this.quantidade = data.quantidade();
        this.unidadeMedida = data.unidadeMedida();
        this.nivel = data.nivel();
        this.tempoPreparacao = data.tempoPreparacao();
        this.tempoProducao = data.tempoProducao();
        this.roteiroId = data.roteiroId();
        this.createdAt = data.createdAt();
    }
}
