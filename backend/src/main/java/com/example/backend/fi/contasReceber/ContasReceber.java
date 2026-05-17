package com.example.backend.fi.contasReceber;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.fi.centrosCusto.CentrosCusto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "contas_receber", schema = "financeiro")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ContasReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresas empresa;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros cliente;

    @ManyToOne
    @JoinColumn(name = "centro_custo_id")
    private CentrosCusto centroCusto;

    @Column(name = "numero_documento")
    private String numeroDocumento;
    private String descricao;
    @Column(name = "valor_original", precision = 15, scale = 2)
    private BigDecimal valorOriginal;
    @Column(name = "valor_recebido", precision = 15, scale = 2)
    private BigDecimal valorRecebido;
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    @Column(name = "data_recebimento")
    private LocalDate dataRecebimento;
    private String status;
    @Column(name = "forma_pagamento")
    private String formaPagamento;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
