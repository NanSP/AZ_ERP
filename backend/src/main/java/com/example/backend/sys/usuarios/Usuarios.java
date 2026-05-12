package com.example.backend.sys.usuarios;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Usuarios(UsuariosRequestDTO data){
        this.nome = data.nome();
        this.email = data.email();
        this.login = data.login();
        this.senhaHash = data.senhaHash();
        this.documento = data.documento();
        this.tipoUsuario = data.tipoUsuario();
        this.status = data.status();
        this.ultimoAcesso = data.ultimoAcesso();
        this.expiracaoSenha = data.expiracaoSenha();
        this.tentativasLogin = data.tentativasLogin();
        this.createdAt = data.createdAt();
    }
}
