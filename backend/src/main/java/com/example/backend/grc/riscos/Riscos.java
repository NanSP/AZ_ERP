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
    private Usuarios responsavel;
    @Column(name = "plano_mitigacao")
    private String planoMitigacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
