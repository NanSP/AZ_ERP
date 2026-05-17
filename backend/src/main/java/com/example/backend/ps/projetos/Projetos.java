package com.example.backend.ps.projetos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.ps.tarefas.Tarefas;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "projetos", schema = "projetos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Projetos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefas> tarefas = new ArrayList<>();

    private String codigo;
    private String nome;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros cliente;

    @ManyToOne
    @JoinColumn(name = "gerente_id")
    private Usuarios gerente;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;
    @Column(name = "data_prevista_inicio")
    private LocalDate dataPrevistaInicio;
    @Column(name = "data_prevista_fim")
    private LocalDate dataPrevistaFim;
    @Column(name = "orcamento_total", precision = 15, scale = 2)
    private BigDecimal orcamentoTotal;
    @Column(name = "orcamento_gasto", precision = 15, scale = 2)
    private BigDecimal orcamentoGasto;
    private String status;
    private Integer prioridade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
