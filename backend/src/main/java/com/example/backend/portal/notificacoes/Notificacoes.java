package com.example.backend.portal.notificacoes;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "notificacoes", schema = "com/example/backend/portal")
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
    private Usuarios usuarioId;
    private String titulo;
    private String mensagem;
    private String tipo;
    private Boolean lida;
    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notificacoes(NotificacoesRequestDTO data) {
        this.usuarioId = data.usuarioId();
        this.titulo = data.titulo();
        this.mensagem = data.mensagem();
        this.tipo = data.tipo();
        this.lida = data.lida();
        this.dataLeitura = data.dataLeitura();
        this.createdAt = data.createdAt();
    }
}
