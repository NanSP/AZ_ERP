package sm.ordensServico;

import core.parceiros.Parceiros;
import core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;

import java.time.LocalDateTime;

@Table(name = "ordens_servico", schema = "servicos")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrdensServico {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_os")
    private String numeroOs;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros clienteId;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtoId;

    @Column(name = "tipo_servico")
    private String tipoServico;
    @Column(name = "descricao_problema")
    private String descricaoProblema;
    private String prioridade;
    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura;
    @Column(name = "data_abertura")
    private LocalDateTime dataAgendamento;
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;
    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Colaboradores tecnicoId;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OrdensServico(OrdensServicoRequestDTO data) {
        this.numeroOs = data.numeroOs();
        this.clienteId = data.clienteId();
        this.tipoServico = data.tipoServico();
        this.produtoId = data.produtoId();
        this.descricaoProblema = data.descricaoProblema();
        this.prioridade = data.prioridade();
        this.dataAgendamento = data.dataAgendamento();
        this.dataAbertura = data.dataAbertura();
        this.dataInicio = data.dataInicio();
        this.dataFim = data.dataFim();
        this.tecnicoId = data.tecnicoId();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
