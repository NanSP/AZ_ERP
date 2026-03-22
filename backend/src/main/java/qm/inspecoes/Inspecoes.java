package qm.inspecoes;

import core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "inspecoes", schema = "qualidade")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Inspecoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_inspecao")
    private String tipoInspecao;
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtoId;

    private String lote;
    @Column(name = "quantidade_inspecionada", precision = 10, scale = 4)
    private BigDecimal quantidadeInspecionada;
    @Column(name = "quantidade_aprovada", precision = 10, scale = 4)
    private BigDecimal quantidadeAprovada;
    @Column(name = "quantidade_reprovada", precision = 10, scale = 4)
    private BigDecimal quantidadeReprovada;
    @Column(name = "data_inspecao")
    private LocalDate dataInspecao;

    @ManyToOne
    @JoinColumn(name = "inspetor_id")
    private Colaboradores inspetorId;

    private String resultado;
    private String observacoes;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Inspecoes(InspecoesRequestDTO data) {

        this.tipoInspecao = data.tipoInspecao();
        this.produtoId = data.produtoId();
        this.lote = data.lote();
        this.quantidadeInspecionada = data.quantidadeInspecionada();
        this.quantidadeAprovada = data.quantidadeAprovada();
        this.quantidadeReprovada = data.quantidadeReprovada();
        this.dataInspecao = data.dataInspecao();
        this.inspetorId = data.inspetorId();
        this.resultado = data.resultado();
        this.observacoes = data.observacoes();
        this.createdAt = data.createdAt();
    }
}
