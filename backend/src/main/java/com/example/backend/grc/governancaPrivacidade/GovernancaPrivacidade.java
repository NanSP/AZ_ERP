package com.example.backend.grc.governancaPrivacidade;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "governanca_privacidade", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class GovernancaPrivacidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_referencia")
    private String nomeReferencia;

    @Column(name = "papel_privacidade")
    private String papelPrivacidade;

    @Column(name = "encarregado_nome")
    private String encarregadoNome;

    @Column(name = "encarregado_email")
    private String encarregadoEmail;

    @Column(name = "encarregado_canal")
    private String encarregadoCanal;

    @Column(name = "base_contratual")
    private String baseContratual;

    @Column(name = "clausulas_contratuais")
    private String clausulasContratuais;

    @Column(name = "suboperadores_declarados")
    private Boolean suboperadoresDeclarados;

    @Column(name = "transferencia_internacional")
    private Boolean transferenciaInternacional;

    @Column(name = "procedimento_incidente")
    private String procedimentoIncidente;

    private Boolean ativo;

    @Column(name = "vigente_desde")
    private LocalDateTime vigenteDesde;

    @Column(name = "revisao_programada_em")
    private LocalDateTime revisaoProgramadaEm;

    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
