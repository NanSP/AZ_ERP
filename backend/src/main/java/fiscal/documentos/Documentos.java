package fiscal.documentos;

import core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "documentos", schema = "fiscal")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Documentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_documento")
    private String tipoDocumento;
    private String numero;
    private String serie;
    @Column(name = "chave_acesso")
    private String chaveAcesso;
    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedidoId;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros clienteId;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
    private String status;
    private String xml_file;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Documentos(DocumentosRequestDTO data) {
        this.tipoDocumento = data.tipoDocumento();
        this.numero = data.numero();
        this.serie = data.serie();
        this.chaveAcesso = data.chaveAcesso();
        this.dataEmissao = data.dataEmissao();
        this.pedidoId = data.pedidoId();
        this.clienteId = data.clienteId();
        this.valorTotal = data.valorTotal();
        this.status = data.status();
        this.xml_file = data.xml_file();
        this.createdAt = data.createdAt();
    }
}
