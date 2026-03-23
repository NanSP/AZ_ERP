package fi.planoContas;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "plano_contas", schema = "contabil")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PlanoContas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String nome;
    @Column(name = "tipo_conta")
    private String tipoConta;
    private String natureza;
    @ManyToOne
    @JoinColumn(name = "conta_pai_id")
    private PlanoContas planoContas;
    private String situacao;

    @OneToMany(mappedBy = "planoContas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoContas> contasFilhas = new ArrayList<>();

    public PlanoContas(PlanoContasRequestDTO data){
        this.codigo = data.codigo();
        this.nome = data.nome();
        this.tipoConta = data.tipoConta();
        this.natureza = data.natureza();
        this.planoContas = data.planoContas();
        this.situacao = data.situacao();
        this.contasFilhas = data.contasFilhas();
    }
}
