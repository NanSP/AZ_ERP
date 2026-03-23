package com.example.backend.pp.apontamentos;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "apontamentos", schema = "producao")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Apontamentos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "op_id")
    private OrdemProducao opId;

    @Column(name = "maquina_id")
    private Integer maquinaId;

    @ManyToOne
    @JoinColumn(name = "operador_id")
    private Colaboradores operadorId;

    @Column(name = "data_hora_inicio")
    private LocalTime dataHoraInicio;
    @Column(name = "data_hora_fim")
    private LocalTime dataHoraFim;

    @Column(name = "quantidade_produzida", precision = 10, scale = 4)
    private BigDecimal quantidadeProduzida;
    @Column(name = "quantidade_refugo", precision = 10, scale = 4)
    private BigDecimal quantidadeRefugo;
    @Column(name = "tempo_parado", precision = 10, scale = 4)
    private BigDecimal tempoParado;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Apontamentos(ApontamentosRequestDTO data) {
        this.opId = data.opId();
        this.maquinaId = data.maquinaId();
        this.operadorId = data.operadorId();
        this.dataHoraFim = data.dataHoraFim();
        this.dataHoraInicio = data.dataHoraInicio();
        this.quantidadeProduzida = data.quantidadeProduzida();
        this.quantidadeRefugo = data.quantidadeRefugo();
        this.tempoParado = data.tempoParado();
        this.observacoes = data.observacoes();
        this.createdAt = data.createdAt();
    }
}
