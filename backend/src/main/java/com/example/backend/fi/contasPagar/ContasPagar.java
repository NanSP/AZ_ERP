package com.example.backend.fi.contasPagar;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.fi.centrosCusto.CentrosCusto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "contas_pagar", schema = "financeiro")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ContasPagar {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresas empresaId;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Parceiros fornecedorId;

    @ManyToOne
    @JoinColumn(name = "centro_custo_id")
    private CentrosCusto centroCustoId;

    @Column(name = "numero_documento")
    private String numeroDocumento;
    private String descricao;
    @Column(name = "valor_original", precision = 10, scale = 2)
    private BigDecimal valorOriginal;
    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago;
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    private String status;
    @Column(name = "forma_pagamento")
    private String formaPagamento;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ContasPagar(ContasPagarRequestDTO data){
        this.empresaId = data.empresaId();
        this.fornecedorId = data.fornecedorId();
        this.numeroDocumento = data.numeroDocumento();
        this.centroCustoId = data.centroCustoId();
        this.descricao = data.descricao();
        this.valorOriginal = data.valorOriginal();
        this.valorPago = data.valorPago();
        this.dataEmissao = data.dataEmissao();
        this.dataVencimento = data.dataVencimento();
        this.dataPagamento = data.dataPagamento();
        this.createdAt = data.createdAt();
        this.formaPagamento = data.formaPagamento();
        this.status = data.status();
    }
}
