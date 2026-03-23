package portal.dispositivos;

import jakarta.persistence.*;
import lombok.*;
import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "dispositivos", schema = "mobile")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Dispositivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;
    @Column(name = "device_id")
    private String deviceId;
    @Column(name = "device_model")
    private String deviceModel;
    @Column(name = "device_platform")
    private String devicePlatform;
    @Column(name = "push_token")
    private String pushToken;
    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;
    private Boolean ativo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Dispositivos(DispositivosRequestDTO data) {
        this.usuarioId = data.usuarioId();
        this.deviceId = data.deviceId();
        this.deviceModel = data.deviceModel();
        this.devicePlatform = data.devicePlatform();
        this.pushToken = data.pushToken();
        this.ultimoAcesso = data.ultimoAcesso();
        this.ativo = data.ativo();
        this.createdAt = data.createdAt();
    }
}
