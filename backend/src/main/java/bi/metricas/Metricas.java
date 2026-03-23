package bi.metricas;

import bi.relatorios.RelatoriosRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "metricas", schema = "bi")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Metricas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;
    private String categoria;
    private String formula;
    @Column(name = "unidade_medida")
    private String unidadeMedida;
    @Column(precision = 10, scale = 2)
    private BigDecimal meta;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Metricas(MetricasRequestDTO data) {
        this.nome = data.nome();
        this.descricao = data.descricao();
        this.categoria = data.categoria();
        this.formula = data.formula();
        this.unidadeMedida = data.unidadeMedida();
        this.meta = data.meta();
        this.createdAt = data.createdAt();
    }
}
