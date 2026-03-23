package com.example.backend.grc.consentimentos;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Table(name = "consentimentos", schema = "com/example/backend/grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Consentimentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "titular_id")
    private Integer titularId;
    @Column(name = "tipo_titular")
    private String tipoTitular;
    private String finalidade;
    @Column(name = "data_consentimento")
    private LocalDateTime dataConsentimento;
    @Column(name = "data_revogacao")
    private LocalDateTime dataRevogacao;
    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    public Consentimentos(ConsentimentosRequestDTO data) {
        this.titularId = data.titularId();
        this.tipoTitular = data.tipoTitular();
        this.finalidade = data.finalidade();
        this.dataConsentimento = data.dataConsentimento();
        this.dataRevogacao = data.dataRevogacao();
        this.ipAddress = data.ipAddress();
        this.userAgent = data.userAgent();
    }
}
