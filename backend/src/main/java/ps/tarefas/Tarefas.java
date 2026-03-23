package ps.tarefas;

import jakarta.persistence.*;
import lombok.*;
import ps.projetos.Projetos;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "tarefas", schema = "projetos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tarefas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projetos projetoId;

    @ManyToOne
    @JoinColumn(name = "tarefa_pai_id")
    private Tarefas tarefaPaiId;

    @OneToMany(mappedBy = "tarefaPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefas> subtarefas = new ArrayList<>();


    private String titulo;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavelId;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    @Column(name = "horas_estimadas", precision = 10, scale = 2)
    private BigDecimal horasEstimadas;
    @Column(name = "horas_realizadas", precision = 10, scale = 2)
    private BigDecimal horasRealizadas;
    @Column(name = "percentual_concluido")
    private Integer percentualConcluido;
    private String status;
    private Integer prioridade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Tarefas(TarefasRequestDTO data) {
        this.projetoId = data.projetoId();
        this.tarefaPaiId = data.tarefaPaiId();
        this.subtarefas = data.subtarefas();
        this.titulo = data.titulo();
        this.descricao = data.descricao();
        this.responsavelId = data.responsavelId();
        this.dataInicio = data.dataInicio();
        this.dataFim = data.dataFim();
        this.horasEstimadas = data.horasEstimadas();
        this.horasRealizadas = data.horasRealizadas();
        this.percentualConcluido = data.percentualConcluido();
        this.status = data.status();
        this.createdAt = data.createdAt();
        this.prioridade = data.prioridade();
    }
}
