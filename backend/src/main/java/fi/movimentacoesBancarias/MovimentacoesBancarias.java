package fi.movimentacoesBancarias;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "movimentacoes_bancarias", schema = "financeiro")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MovimentacoesBancarias {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "conta_bancaria_id")
    private Integer contaBancariaId;
    @Column(name = "tipo_movimento")
    private String tipoMovimento;
    @Column(precision = 10, scale = 2)
    private BigDecimal valor;
    @Column(name = "data_movimento")
    private LocalDate dataMovimento;
    private String historico;
    @Column(name = "documento_vinculado")
    private String documentoVinculado;
    private Boolean conciliado;
    @Column(name = "data_conciliacao")
    private LocalDate dataConciliacao;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public MovimentacoesBancarias(MovimentacoesBancariasRequestDTO data){
        this.contaBancariaId = data.contaBancariaId();
        this.tipoMovimento = data.tipoMovimento();
        this.valor = data.valor();
        this.dataMovimento = data.dataMovimento();
        this.historico = data.historico();
        this.documentoVinculado = data.documentoVinculado();
        this.conciliado = data.conciliado();
        this.dataConciliacao = data.dataConciliacao();
        this.createdAt = data.createdAt();
    }

}
