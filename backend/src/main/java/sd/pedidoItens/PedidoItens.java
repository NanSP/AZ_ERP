package sd.pedidoItens;

import core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import sd.pedidos.Pedidos;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Table(name = "pedidoItens", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PedidoItens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedidoId;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtoId;
    @Column(precision = 10, scale = 4)
    private BigInteger quantidade;
    @Column(name ="valor_unitario",precision = 10, scale = 4)
    private BigInteger valorUnitario;
    @Column(name ="valor_total",precision = 10, scale = 4)
    private BigInteger valorTotal;
    @Column(precision = 10, scale = 4)
    private BigInteger desconto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PedidoItens(PedidoItensRequestDTO data) {
        this.pedidoId = data.pedidoId();
        this.produtoId = data.produtoId();
        this.quantidade = data.quantidade();
        this.valorUnitario = data.valorUnitario();
        this.valorTotal = data.valorTotal();
        this.desconto = data.desconto();
        this.createdAt = data.createdAt();
    }
}
