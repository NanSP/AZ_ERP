package fiscal.esocialEventos;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "esocial_eventos", schema = "sped")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class EsocialEventos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "periodo_apuracao")
    private LocalDate periodoApuracao;
    @Column(name = "tipo_evento")
    private String tipoEvento;
    @Column(name = "evento_id")
    private String eventoId;
    @JdbcTypeCode(SqlTypes.SQLXML)
    @Column(name = "conteudo",columnDefinition = "xml")
    private String conteudo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public EsocialEventos(EsocialEventosRequestDTO data) {
        this.periodoApuracao = data.periodoApuracao();
        this.tipoEvento = data.tipoEvento();
        this.eventoId = data.eventoId();
        this.conteudo = data.conteudo();
        this.createdAt = data.createdAt();
    }
}
