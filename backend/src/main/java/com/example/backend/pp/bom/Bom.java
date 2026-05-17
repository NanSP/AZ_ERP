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
    private Produtos produtoPai;

    @ManyToOne
    @JoinColumn(name = "componente_id")
    private Produtos componente;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "unidade_medida")
    private String unidadeMedida;
    private Integer nivel;
    @Column(name = "tempo_preparacao",precision = 10, scale = 2)
    private BigDecimal tempoPreparacao;
    @Column(name = "tempo_producao",precision = 10, scale = 2)
    private BigDecimal tempoProducao;
    @Column(name = "roteiro_id")
    private Integer roteiro;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
