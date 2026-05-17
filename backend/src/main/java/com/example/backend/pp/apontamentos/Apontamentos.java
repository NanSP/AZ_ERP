package com.example.backend.pp.apontamentos;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private OrdemProducao op;

    @Column(name = "maquina_id")
    private Integer maquinaId;

    @ManyToOne
    @JoinColumn(name = "operador_id")
    private Colaboradores operador;

    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;
    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "quantidade_produzida", precision = 15, scale = 4)
    private BigDecimal quantidadeProduzida;
    @Column(name = "quantidade_refugo", precision = 15, scale = 4)
    private BigDecimal quantidadeRefugo;
    @Column(name = "tempo_parado", precision = 10, scale = 2)
    private BigDecimal tempoParado;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
