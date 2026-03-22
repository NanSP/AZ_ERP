package sd.clientes;

import core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "compras", schema = "crm")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Clientes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parceiro_id")
    private Parceiros parceiroId;
    private String classificacao;
    private String origem;
    private String website;
    @Column(name = "faturamento_anual", precision = 10, scale = 2)
    private BigDecimal faturamentoAnual;
    @Column(name = "numero_funcionarios")
    private Integer numeroFuncionarios;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Clientes(ClientesRequestDTO data) {
        this.parceiroId = data.parceiroId();
        this.classificacao = data.classificacao();
        this.origem = data.origem();
        this.website = data.website();
        this.faturamentoAnual = data.faturamentoAnual();
        this.numeroFuncionarios = data.numeroFuncionarios();
        this.createdAt = data.createdAt();
    }
}
