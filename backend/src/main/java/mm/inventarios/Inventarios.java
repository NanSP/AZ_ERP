package mm.inventarios;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "inventarios", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Inventarios {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    @Column(name = "tipo_inventario")
    private String tipoInventario;
    private String status;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Inventarios(InventariosRequestDTO data) {
        this.dataInicio = data.dataInicio();
        this.tipoInventario = data.tipoInventario();
        this.dataFim = data.dataFim();
        this.status = data.status();
        this.observacoes = data.observacoes();
        this.createdAt = data.createdAt();
    }
}
