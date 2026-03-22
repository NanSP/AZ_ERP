package sd.oportunidades;

import jakarta.persistence.*;
import lombok.*;
import sd.clientes.Clientes;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "oportunidades", schema = "crm")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Oportunidades {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes clienteId;
    private String titulo;
    private String descricao;
    @Column(name = "valor_estimado", precision = 10, scale = 2)
    private BigDecimal valorEstimado;
    private Integer probabilidade;
    private String estagio;
    @Column(name = "data_prevista_fechamento")
    private LocalDate dataPrevistaFechamento;
    @Column(name = "motivo_perda")
    private String motivoPerda;
    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavelId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Oportunidades(OportunidadesRequestDTO data) {
        this.clienteId = data.clienteId();
        this.titulo = data.titulo();
        this.descricao = data.descricao();
        this.valorEstimado = data.valorEstimado();
        this.estagio = data.estagio();
        this.probabilidade = data.probabilidade();
        this.dataPrevistaFechamento = data.dataPrevistaFechamento();
        this.motivoPerda = data.motivoPerda();
        this.responsavelId = data.responsavelId();
        this.createdAt = data.createdAt();
    }
}
