package com.example.backend.portal.sessoes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Table(name = "sessoes", schema = "com/example/backend/portal")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sessoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;
    @Column(name = "token_sessao")
    private String tokenSessao;
    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "data_login")
    private LocalDateTime dataLogin;
    @Column(name = "data_logout")
    private LocalDateTime dataLogout;
    private LocalDateTime expiracao;

    public Sessoes(SessoesRequestDTO data) {
        this.usuarioId = data.usuarioId();
        this.tokenSessao = data.tokenSessao();
        this.ipAddress = data.ipAddress();
        this.dataLogin = data.dataLogin();
        this.userAgent = data.userAgent();
        this.dataLogout = data.dataLogout();
        this.expiracao = data.expiracao();
    }
}
