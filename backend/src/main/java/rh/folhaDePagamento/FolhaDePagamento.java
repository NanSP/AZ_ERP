package rh.folhaDePagamento;

import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "folha_pagmento", schema = "rh")
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
    private Colaboradores colaboradorId;

    private LocalDate competencia;
    @Column(name = "salario_base", precision = 10, scale = 2)
    private BigDecimal salarioBase;
    @Column(name = "horas_normais", precision = 10, scale = 2)
    private BigDecimal horasNormais;
    @Column(name = "horas_extras", precision = 10, scale = 2)
    private BigDecimal horasExtras;
    @Column(precision = 10, scale = 2)
    private BigDecimal adicionais;
    @Column(precision = 10, scale = 2)
    private BigDecimal descontos;
    @Column(name = "valor_liquido", precision = 10, scale = 2)
    private BigDecimal valorLiquido;
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public FolhaDePagamento(FolhaDePagamentoRequestDTO data){
        this.colaboradorId = data.colaboradorId();
        this.competencia = data.competencia();
        this.salarioBase = data.salarioBase();
        this.horasNormais = data.horasNormais();
        this.horasExtras = data.horasExtras();
        this.adicionais = data.adicionais();
        this.descontos = data.descontos();
        this.valorLiquido = data.valorLiquido();
        this.dataPagamento = data.dataPagamento();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
