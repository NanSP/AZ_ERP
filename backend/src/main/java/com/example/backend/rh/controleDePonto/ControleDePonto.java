package com.example.backend.rh.controleDePonto;

import com.example.backend.rh.colaboradores.Colaboradores;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "ponto_eletronico", schema = "com/example/backend/rh")
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
    private Colaboradores colaboradorId;

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

    public ControleDePonto(ControleDePontoRequestDTO data){
        this.colaboradorId = data.colaboradorId();
        this.data = data.data();
        this.horaEntrada = data.horaEntrada();
        this.horaSaidaAlmoco = data.horaSaidaAlmoco();
        this.horaRetornoAlmoco = data.horaRetornoAlmoco();
        this.horaSaida = data.horaSaida();
        this.horasTrabalhadas = data.horasTrabalhadas();
        this.horasExtras = data.horasExtras();
        this.atrasos = data.atrasos();
        this.createdAt = data.createdAt();
    }
}
