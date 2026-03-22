package am.bensPatrimoniais;

import core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;

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
    @Column(name = "valor_aquisicao", precision = 10, scale = 2)
    private BigDecimal valorAquisicao;
    @Column(name = "valor_atual", precision = 10, scale = 2)
    private BigDecimal valorAtual;
    @Column(name = "vida_util_anos")
    private Integer vidaUtilAnos;
    @Column(name = "taxa_depreciacao", precision = 10, scale = 2)
    private BigDecimal taxaDepreciacao;
    @Column(name = "data_depreciacao")
    private LocalDate dataDepreciacao;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Parceiros fornecedorId;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Colaboradores responsavelId;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BensPatrimoniais(BensPatrimoniaisRequestDTO data) {
        this.codigoPatrimonio = data.codigoPatrimonio();
        this.nome = data.nome();
        this.tipoAtivo = data.tipoAtivo();
        this.descricao = data.descricao();
        this.localizacao = data.localizacao();
        this.dataAquisicao = data.dataAquisicao();
        this.valorAquisicao = data.valorAquisicao();
        this.valorAtual = data.valorAtual();
        this.vidaUtilAnos = data.vidaUtilAnos();
        this.taxaDepreciacao = data.taxaDepreciacao();
        this.dataDepreciacao = data.dataDepreciacao();
        this.fornecedorId = data.fornecedorId();
        this.responsavelId = data.responsavelId();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
