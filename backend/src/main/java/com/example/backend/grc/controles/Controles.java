package com.example.backend.grc.controles;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "controles", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Controles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String descricao;
    @Column(name = "tipo_controle")
    private String tipoControle;
    private String frequencia;
    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavelId;

    private Boolean efetivo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Controles(ControlesRequestDTO data) {
        this.codigo = data.codigo();
        this.descricao = data.descricao();
        this.tipoControle = data.tipoControle();
        this.frequencia = data.frequencia();
        this.responsavelId = data.responsavelId();
        this.efetivo = data.efetivo();
        this.createdAt = data.createdAt();
    }
}
