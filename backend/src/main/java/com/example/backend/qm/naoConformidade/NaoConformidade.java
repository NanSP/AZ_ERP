package com.example.backend.qm.naoConformidade;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.qm.inspecoes.Inspecoes;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "nao_conformidades", schema = "qualidade")
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
    private Inspecoes inspecao;
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
    private Colaboradores responsavel;
    @Column(name = "data_identificacao")
    private LocalDate dataIdentificacao;
    @Column(name = "data_resolucao")
    private LocalDate dataResolucao;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
