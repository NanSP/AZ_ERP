package com.example.backend.sys.usuarios;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "usuarios", schema = "sys")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuarios {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;
    private String login;
    @Column(name = "senha_hash")
    private String senhaHash;
    private String documento;
    @Column(name = "tipo_usuario")
    private String tipoUsuario;
    private String status;
    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;
    @Column(name = "expiracao_senha")
    private LocalDate expiracaoSenha;
    @Column(name = "tentativas_login")
    private Integer tentativasLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
