package com.example.backend.portal.notificacoes;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "notificacoes", schema = "portal")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Notificacoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuario;
    private String titulo;
    private String mensagem;
    private String tipo;
    private Boolean lida;
    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
