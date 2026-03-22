package pp.ordemProducao;

import core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "ordens_producao", schema = "producao")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrdemProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_op")
    private String numeroOp;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtoId;
    @Column(name = "quantidade_planejada",precision = 10, scale = 4)
    private BigDecimal quantidadePlanejada;
    @Column(name = "quantidade_produzida",precision = 10, scale = 4)
    private BigDecimal quantidadeProduzida;
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    @Column(name = "data_prevista")
    private LocalDate dataPrevista;
    private String status;
    private Integer prioridade;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OrdemProducao(OrdemProducaoRequestDTO data) {
        this.numeroOp = data.numeroOp();
        this.produtoId = data.produtoId();
        this.quantidadePlanejada = data.quantidadePlanejada();
        this.quantidadeProduzida = data.quantidadeProduzida();
        this.dataInicio = data.dataInicio();
        this.dataEmissao = data.dataEmissao();
        this.dataFim = data.dataFim();
        this.dataPrevista = data.dataPrevista();
        this.status = data.status();
        this.observacoes = data.observacoes();
        this.prioridade = data.prioridade();
        this.createdAt = data.createdAt();
    }
}
