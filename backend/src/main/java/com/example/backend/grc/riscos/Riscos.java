package com.example.backend.grc.riscos;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "riscos", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Riscos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String titulo;
    private String descricao;
    private String categoria;

    private Integer probabilidade;
    private Integer impacto;
    @Column(name = "nivel_risco")
    private String nivelRisco;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavelId;
    @Column(name = "plano_mitigacao")
    private String planoMitigacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Riscos(RiscosRequestDTO data) {
        this.codigo = data.codigo();
        this.titulo = data.titulo();
        this.descricao = data.descricao();
        this.probabilidade = data.probabilidade();
        this.categoria = data.categoria();
        this.impacto = data.impacto();
        this.nivelRisco = data.nivelRisco();
        this.responsavelId = data.responsavelId();
        this.planoMitigacao = data.planoMitigacao();
        this.createdAt = data.createdAt();
    }
}
