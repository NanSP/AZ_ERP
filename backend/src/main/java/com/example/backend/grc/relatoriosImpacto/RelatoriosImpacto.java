package com.example.backend.grc.relatoriosImpacto;

import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "relatorios_impacto_privacidade", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class RelatoriosImpacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    @Column(name = "escopo_critico")
    private String escopoCritico;

    @Column(name = "prioridade_risco")
    private String prioridadeRisco;

    private String modulo;
    private String recurso;
    private String finalidade;

    @Column(name = "dados_pessoais_envolvidos")
    private String dadosPessoaisEnvolvidos;

    @Column(name = "dados_sensiveis")
    private Boolean dadosSensiveis;

    @Column(name = "base_legal")
    private String baseLegal;

    @Column(name = "volume_titulares")
    private Integer volumeTitulares;

    @Column(name = "compartilhamento_externo")
    private Boolean compartilhamentoExterno;

    @Column(name = "medidas_tecnicas")
    private String medidasTecnicas;

    @Column(name = "medidas_organizacionais")
    private String medidasOrganizacionais;

    @Column(name = "risco_residual")
    private String riscoResidual;

    private String decisao;

    @ManyToOne
    @JoinColumn(name = "aprovado_por_id")
    private Usuarios aprovadoPor;

    @Column(name = "revisado_em")
    private LocalDateTime revisadoEm;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
