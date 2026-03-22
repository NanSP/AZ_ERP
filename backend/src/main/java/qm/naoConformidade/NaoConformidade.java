package qm.naoConformidade;

import jakarta.persistence.*;
import lombok.*;
import qm.inspecoes.Inspecoes;
import qm.inspecoes.InspecoesRequestDTO;
import rh.colaboradores.Colaboradores;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "naoConformidade", schema = "qualidade")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class NaoConformidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inspecao_id")
    private Inspecoes inspecaoId;
    @Column(name = "tipo_nao_conformidade")
    private String tipoNaoConformidade;
    private String descricao;
    @Column(name = "causa_raiz")
    private String causaRaiz;
    @Column(name = "acao_imediata")
    private String acaoImediata;
    @Column(name = "acao_corretiva")
    private String acaoCorretiva;
    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Colaboradores responsavelId;
    @Column(name = "data_identificacao")
    private LocalDate dataIdentificacao;
    @Column(name = "data_resolucao")
    private LocalDate dataResolucao;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public NaoConformidade(NaoConformidadeRequestDTO data) {
        this.inspecaoId = data.inspecaoId();
        this.tipoNaoConformidade = data.tipoNaoConformidade();
        this.descricao = data.descricao();
        this.causaRaiz = data.causaRaiz();
        this.acaoImediata = data.acaoImediata();
        this.acaoCorretiva = data.acaoCorretiva();
        this.responsavelId = data.responsavelId();
        this.dataIdentificacao = data.dataIdentificacao();
        this.dataResolucao = data.dataResolucao();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
