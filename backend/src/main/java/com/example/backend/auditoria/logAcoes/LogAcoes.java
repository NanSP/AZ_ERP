package com.example.backend.auditoria.logAcoes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "log_acoes", schema = "com/example/backend/auditoria")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class LogAcoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;
    private String modulo;
    private String acao;
    private String tabela;
    @Column(name = "registro_id")
    private Integer registroId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_antigos",columnDefinition = "jsonb")
    private Map<String, Object> dadosAntigos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_novos",columnDefinition = "jsonb")
    private Map<String, Object> dadosNovos;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;
    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public LogAcoes(LogAcoesRequestDTO data) {
        this.usuarioId = data.usuarioId();
        this.modulo = data.modulo();
        this.acao = data.acao();
        this.tabela = data.tabela();
        this.registroId = data.registroId();
        this.dadosNovos = data.dadosNovos();
        this.dadosAntigos = data.dadosAntigos();
        this.ipAddress = data.ipAddress();
        this.userAgent = data.userAgent();
        this.createdAt = data.createdAt();
    }

}
