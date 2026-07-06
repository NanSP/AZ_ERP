package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.grc.consentimentos.Consentimentos;
import com.example.backend.grc.registrosTratamento.RegistrosTratamento;
import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "solicitacoes_titular", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitacoesTitular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String protocolo;

    @Column(name = "titular_nome")
    private String titularNome;

    @Column(name = "titular_contato")
    private String titularContato;

    @Column(name = "tipo_titular")
    private String tipoTitular;

    @Column(name = "direito_solicitado")
    private String direitoSolicitado;

    private String modulo;
    private String entidade;
    private String status;

    @Column(name = "canal_origem")
    private String canalOrigem;

    private String detalhes;

    @Column(name = "prazo_resposta")
    private LocalDateTime prazoResposta;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "resposta_resumo")
    private String respostaResumo;

    @ManyToOne
    @JoinColumn(name = "atendido_por_id")
    private Usuarios atendidoPor;

    @ManyToOne
    @JoinColumn(name = "registro_tratamento_id")
    private RegistrosTratamento registroTratamento;

    @ManyToOne
    @JoinColumn(name = "consentimento_id")
    private Consentimentos consentimento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
