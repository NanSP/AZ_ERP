package auditoria.logErros;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "log_erros", schema = "auditoria")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class LogErros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "erro_codigo")
    private Integer erroCodigo;
    @Column(name = "erro_mensagem")
    private Integer erroMensagem;
    private String modulo;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;

    private String url;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parametros",columnDefinition = "jsonb")
    private Map<String, Object> parametros;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public LogErros(LogErrosRequestDTO data) {
        this.erroCodigo = data.erroCodigo();
        this.erroMensagem = data.erroMensagem();
        this.usuarioId = data.usuarioId();
        this.modulo = data.modulo();
        this.url = data.url();
        this.parametros = data.parametros();
        this.ipAddress = data.ipAddress();
        this.createdAt = data.createdAt();
    }
}
