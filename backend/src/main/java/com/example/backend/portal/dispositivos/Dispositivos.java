package com.example.backend.portal.dispositivos;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sys.usuarios.Usuarios;

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

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuario;
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

}
