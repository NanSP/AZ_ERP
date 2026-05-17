package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.tarefas.Tarefas;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.ps.projetos.Projetos;

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
    private Projetos projeto;

    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private Tarefas tarefa;
    @Column(name = "tipo_recurso")
    private String tipoRecurso;
    @Column(name = "recurso_id")
    private Integer recursoId;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "valor_unitario",precision = 15, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "valor_total",precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "data_alocacao")
    private LocalDate dataAlocacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
