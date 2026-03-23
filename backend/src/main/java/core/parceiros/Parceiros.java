package core.parceiros;

import fi.contasPagar.ContasPagar;
import fi.contasReceber.ContasReceber;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "parceiros", schema = "core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Parceiros {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_parceiro")
    private String tipoParceiro;
    private String codigo;
    private String nome;
    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    private String documento;
    @Column(name = "tipo_pessoa")
    private String tipoPessoa;
    private String situacao;
    @Column(name = "limite_credito", precision = 10, scale = 2)
    private BigDecimal limiteCredito;
    @Column(name = "dias_prazo")
    private Integer diasPrazo;
    private String observacoes;

    @OneToMany(mappedBy = "fornecedorId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContasPagar> contasAPagar = new ArrayList<>();

    @OneToMany(mappedBy = "clienteId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContasReceber> contasAReceber = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Parceiros(ParceirosRequestDTO data){
        this.tipoParceiro = data.tipoParceiro();
        this.codigo = data.codigo();
        this.nome = data.nome();
        this.nomeFantasia = data.nomeFantasia();
        this.documento = data.documento();
        this.tipoPessoa = data.tipoPessoa();
        this.situacao = data.situacao();
        this.limiteCredito = data.limiteCredito();
        this.diasPrazo = data.diasPrazo();
        this.observacoes = data.observacoes();
        this.contasAPagar = data.contasAPagar();
        this.contasAReceber = data.contasAReceber();
        this.createdAt = data.createdAt();
    }
}
