package com.example.backend.rh.folhaDePagamento;

import com.example.backend.rh.colaboradores.Colaboradores;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "folha_pagamento", schema = "rh")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FolhaDePagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaboradores colaborador;

    private LocalDate competencia;

    @Column(name = "salario_base", precision = 15, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "horas_normais", precision = 10, scale = 2)
    private BigDecimal horasNormais;

    @Column(name = "horas_extras", precision = 10, scale = 2)
    private BigDecimal horasExtras;

    @Column(precision = 15, scale = 2)
    private BigDecimal adicionais;

    @Column(precision = 15, scale = 2)
    private BigDecimal descontos;

    @Column(name = "valor_hora", precision = 15, scale = 2)
    private BigDecimal valorHora;

    @Column(name = "valor_horas_normais", precision = 15, scale = 2)
    private BigDecimal valorHorasNormais;

    @Column(name = "valor_horas_extras", precision = 15, scale = 2)
    private BigDecimal valorHorasExtras;

    @Column(name = "valor_bruto", precision = 15, scale = 2)
    private BigDecimal valorBruto;

    @Column(name = "valor_liquido", precision = 15, scale = 2)
    private BigDecimal valorLiquido;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}