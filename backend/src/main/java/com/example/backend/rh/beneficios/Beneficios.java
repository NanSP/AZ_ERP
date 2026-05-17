package com.example.backend.rh.beneficios;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "beneficios", schema = "rh")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Beneficios {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaboradores colaborador;

    @Column(name = "tipo_beneficio")
    private String tipoBeneficio;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    @Column(columnDefinition = "boolean default true")
    private Boolean ativo;

}
