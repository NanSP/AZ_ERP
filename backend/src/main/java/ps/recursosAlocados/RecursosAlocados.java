package ps.recursosAlocados;

import jakarta.persistence.*;
import lombok.*;
import ps.projetos.Projetos;
import ps.tarefas.Tarefas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "recursos_alocados", schema = "projetos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class RecursosAlocados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projetos projetoId;

    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private Tarefas tarefaId;
    @Column(name = "tipo_recurso")
    private String tipoRecurso;
    @Column(name = "recurso_id")
    private Integer recursoId;
    @Column(precision = 10, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "valor_unitario",precision = 10, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "valor_total",precision = 10, scale = 4)
    private BigDecimal valorTotal;
    @Column(name = "data_alocacao")
    private LocalDate dataAlocacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public RecursosAlocados(RecursosAlocadosRequestDTO data) {
        this.projetoId = data.projetoId();
        this.tarefaId = data.tarefaId();
        this.tipoRecurso = data.tipoRecurso();
        this.recursoId = data.recursoId();
        this.quantidade = data.quantidade();
        this.valorUnitario = data.valorUnitario();
        this.valorTotal = data.valorTotal();
        this.dataAlocacao = data.dataAlocacao();
        this.createdAt = data.createdAt();
    }
}
