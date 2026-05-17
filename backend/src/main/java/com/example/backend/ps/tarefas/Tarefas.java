package com.example.backend.ps.tarefas;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.ps.projetos.Projetos;
import com.example.backend.sys.usuarios.Usuarios;

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
    private Projetos projeto;

    @ManyToOne
    @JoinColumn(name = "tarefa_pai_id")
    private Tarefas tarefaPai;

    @OneToMany(mappedBy = "tarefaPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefas> subtarefas = new ArrayList<>();


    private String titulo;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

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
}
