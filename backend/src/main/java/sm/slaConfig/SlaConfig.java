package sm.slaConfig;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "sla_config", schema = "servicos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SlaConfig {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_servico")
    private String tipoServico;
    private String prioridade;
    @Column(name = "tempo_atendimento_horas")
    private Integer tempoAtendimentoHoras;
    @Column(name = "tempo_resolucao_horas")
    private Integer tempoResolucaoHoras;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public SlaConfig(SlaConfigRequestDTO data) {
        this.tipoServico = data.tipoServico();
        this.prioridade = data.prioridade();
        this.tempoAtendimentoHoras = data.tempoAtendimentoHoras();
        this.tempoResolucaoHoras = data.tempoResolucaoHoras();
        this.createdAt = data.createdAt();
    }
}
