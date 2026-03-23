package grc.auditorias;

import jakarta.persistence.*;
import lombok.*;
import sys.usuarios.Usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "dashboards", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Auditorias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    @Column(name = "tipo_auditoria")
    private String tipoAuditoria;
    private String escopo;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavelId;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Auditorias(AuditoriasRequestDTO data) {
        this.titulo = data.titulo();
        this.tipoAuditoria = data.tipoAuditoria();
        this.escopo = data.escopo();
        this.dataInicio = data.dataInicio();
        this.dataFim = data.dataFim();
        this.responsavelId = data.responsavelId();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
