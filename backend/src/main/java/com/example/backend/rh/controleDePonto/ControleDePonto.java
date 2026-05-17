package com.example.backend.rh.controleDePonto;

import com.example.backend.rh.colaboradores.Colaboradores;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "ponto_eletronico", schema = "rh")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ControleDePonto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaboradores colaborador;

    private LocalDate data;
    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;
    @Column(name = "hora_saida_almoco")
    private LocalTime horaSaidaAlmoco;
    @Column(name = "hora_retorno_almoco")
    private LocalTime horaRetornoAlmoco;
    @Column(name = "hora_saida")
    private LocalTime horaSaida;
    @Column(name = "horas_trabalhadas")
    private BigDecimal horasTrabalhadas;
    @Column(name = "horas_extras")
    private BigDecimal horasExtras;
    private Integer atrasos;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
