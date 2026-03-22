package sd.faturas;

import jakarta.persistence.*;
import lombok.*;
import sd.pedidos.Pedidos;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "faturas", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Faturas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedidoId;
    @Column(name = "numero_fatura")
    private String numeroFatura;
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigInteger valorTotal;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Faturas(FaturasRequestDTO data) {
        this.pedidoId = data.pedidoId();
        this.numeroFatura = data.numeroFatura();
        this.dataEmissao = data.dataEmissao();
        this.valorTotal = data.valorTotal();
        this.dataVencimento = data.dataVencimento();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
