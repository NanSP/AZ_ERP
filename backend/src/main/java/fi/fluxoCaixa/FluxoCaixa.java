package fi.fluxoCaixa;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "fluxo_caixa", schema = "financeiro")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FluxoCaixa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_referencia")
    private LocalDate dataReferencia;
    @Column(name = "saldo_inicial", precision = 10, scale = 2)
    private BigDecimal saldoInicial;
    @Column(name = "entradas_previstas", precision = 10, scale = 2)
    private BigDecimal entradasPrevistas;
    @Column(name = "saidas_previstas", precision = 10, scale = 2)
    private BigDecimal saidasPrevistas;
    @Column(name = "entradas_realizadas", precision = 10, scale = 2)
    private BigDecimal entradasRealizadas;
    @Column(name = "saidas_realizadas", precision = 10, scale = 2)
    private BigDecimal saidasRealizadas;
    @Column(name = "saldo_final_previsto", precision = 10, scale = 2)
    private BigDecimal saldoFinalPrevisto;
    @Column(name = "saldo_final_real", precision = 10, scale = 2)
    private BigDecimal saldoFinalReal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public FluxoCaixa(FluxoCaixaRequestDTO data){
        this.saldoInicial = data.saldoInicial();
        this.entradasPrevistas = data.entradasPrevistas();
        this.saidasPrevistas = data.saidasPrevistas();
        this.entradasRealizadas = data.entradasRealizadas();
        this.saidasRealizadas = data.saidasRealizadas();
        this.saldoFinalPrevisto = data.saldoFinalPrevisto();
        this.saldoFinalReal = data.saldoFinalReal();
        this.createdAt = data.createdAt();
        this.dataReferencia = data.dataReferencia();
    }
}
