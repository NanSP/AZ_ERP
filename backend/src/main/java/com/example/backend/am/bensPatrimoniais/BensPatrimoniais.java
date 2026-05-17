package com.example.backend.am.bensPatrimoniais;

import com.example.backend.core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "bens_patrimoniais", schema = "ativos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class BensPatrimoniais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_patrimonio")
    private String codigoPatrimonio;
    private String nome;
    private String descricao;
    @Column(name = "tipo_ativo")
    private String tipoAtivo;
    private String localizacao;
    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;
    @Column(name = "valor_aquisicao", precision = 15, scale = 2)
    private BigDecimal valorAquisicao;
    @Column(name = "valor_atual", precision = 15, scale = 2)
    private BigDecimal valorAtual;
    @Column(name = "vida_util_anos")
    private Integer vidaUtilAnos;
    @Column(name = "taxa_depreciacao", precision = 5, scale = 2)
    private BigDecimal taxaDepreciacao;
    @Column(name = "data_depreciacao")
    private LocalDate dataDepreciacao;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Parceiros fornecedor;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Colaboradores responsavel;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
