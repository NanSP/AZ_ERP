package am.manutencoes;


import am.bensPatrimoniais.BensPatrimoniais;
import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "manutencoes", schema = "ativos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Manutencoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ativo_id")
    private BensPatrimoniais ativoId;
    @Column(name = "tipo_manutencao")
    private String tipoManutencao;
    @Column(name = "data_solicitacao")
    private LocalDate dataSolicitacao;
    @Column(name = "data_execucao")
    private LocalDate dataExecucao;
    private String descricao;
    @Column(name = "custo_mao_obra", precision = 10, scale = 2)
    private BigDecimal custoMaoObra;
    @Column(name = "custo_material", precision = 10, scale = 2)
    private BigDecimal custoMaterial;
    @Column(name = "custo_total", precision = 10, scale = 2)
    private BigDecimal custoTotal;
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Colaboradores tecnicoId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Manutencoes(ManutencoesRequestDTO data) {
        this.ativoId = data.ativoId();
        this.tipoManutencao = data.tipoManutencao();
        this.dataSolicitacao = data.dataSolicitacao();
        this.dataExecucao = data.dataExecucao();
        this.custoMaoObra = data.custoMaoObra();
        this.descricao = data.descricao();
        this.custoMaterial = data.custoMaterial();
        this.custoTotal = data.custoTotal();
        this.tecnicoId = data.tecnicoId();
        this.createdAt = data.createdAt();
    }
}
