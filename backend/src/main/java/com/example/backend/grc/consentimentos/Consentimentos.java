package com.example.backend.grc.consentimentos;

import com.example.backend.grc.registrosTratamento.RegistrosTratamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Table(name = "consentimentos", schema = "grc")
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
    private Integer titular;
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

    @ManyToOne
    @JoinColumn(name = "registro_tratamento_id")
    private RegistrosTratamento registroTratamento;

}
