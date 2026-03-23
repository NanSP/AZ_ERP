package com.example.backend.sys.usuarios;

import com.example.backend.auditoria.logAcoes.LogAcoes;
import com.example.backend.auditoria.logErros.LogErros;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.portal.dispositivos.Dispositivos;
import com.example.backend.portal.notificacoes.Notificacoes;
import com.example.backend.portal.sessoes.Sessoes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sessoes> sessoes = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogAcoes> logAcoes = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogErros> logErros = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacoes> notificacoes = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dispositivos> dispositivos = new ArrayList<>();

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
        this.sessoes = data.sessoes();
        this.logAcoes = data.logAcoes();
        this.logErros = data.logErros();
        this.notificacoes = data.notificacoes();
        this.dispositivos = data.dispositivos();
        this.createdAt = data.createdAt();
    }
}
