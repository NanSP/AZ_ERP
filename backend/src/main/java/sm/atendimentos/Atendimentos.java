package sm.atendimentos;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import rh.colaboradores.Colaboradores;
import sm.ordensServico.OrdensServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "atendimentos", schema = "servicos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Atendimentos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "os_id")
    private OrdensServico osId;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Colaboradores tecnicoId;
    @Column(name = "data_hora")
    private LocalDateTime dataHora;
    private String descricao;
    @Column(name = "horas_gastas", precision = 10, scale = 2)
    private BigDecimal horasGastas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "materiais_utilizados",columnDefinition = "jsonb")
    private Map<String, Object> materiaisUtilizados;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Atendimentos(AtendimentosRequestDTO data) {
        this.osId = data.osId();
        this.tecnicoId = data.tecnicoId();
        this.dataHora = data.dataHora();
        this.descricao = data.descricao();
        this.horasGastas = data.horasGastas();
        this.materiaisUtilizados = data.materiaisUtilizados();
        this.createdAt = data.createdAt();
    }
}
