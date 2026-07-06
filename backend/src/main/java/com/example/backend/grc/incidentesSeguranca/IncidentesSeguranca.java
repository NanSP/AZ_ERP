package com.example.backend.grc.incidentesSeguranca;

import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "incidentes_seguranca", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class IncidentesSeguranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String titulo;

    @Column(name = "escopo_critico")
    private String escopoCritico;

    private String severidade;

    @Column(name = "etapa_atual")
    private String etapaAtual;

    @Column(name = "origem_deteccao")
    private String origemDeteccao;

    @Column(name = "resumo_incidente")
    private String resumoIncidente;

    @Column(name = "dados_afetados")
    private String dadosAfetados;

    @Column(name = "titulares_estimados")
    private Integer titularesEstimados;

    @Column(name = "segredo_tecnico_exposto")
    private Boolean segredoTecnicoExposto;

    @Column(name = "requer_comunicacao_anpd")
    private Boolean requerComunicacaoAnpd;

    @Column(name = "requer_comunicacao_titulares")
    private Boolean requerComunicacaoTitulares;

    @Column(name = "data_deteccao")
    private LocalDateTime dataDeteccao;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @Column(name = "data_comunicacao")
    private LocalDateTime dataComunicacao;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    @Column(name = "causa_raiz")
    private String causaRaiz;

    @Column(name = "acoes_contencao")
    private String acoesContencao;

    @Column(name = "acoes_corretivas")
    private String acoesCorretivas;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
