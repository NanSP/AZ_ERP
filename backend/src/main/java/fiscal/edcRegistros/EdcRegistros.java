package fiscal.edcRegistros;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "ecd_registros", schema = "sped")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class EdcRegistros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime periodo;
    private String registro;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conteudo",columnDefinition = "jsonb")
    private Map<String, Object> conteudo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public EdcRegistros(EdcRegistrosRequestDTO data) {
        this.periodo = data.periodo();
        this.registro = data.registro();
        this.conteudo = data.conteudo();
        this.createdAt = data.createdAt();
    }
}
