package mm.compraItens;

import core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import mm.compras.Compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "compra_itens", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompraItens {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compras compraId;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produtoId;
    @Column(precision = 10, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "valor_unitario", precision = 10, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "valor_total", precision = 10, scale = 4)
    private BigDecimal valorTotal;
    @Column(name = "quantidade_recebida", precision = 10, scale = 4)
    private BigDecimal quantidadeRecebida;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CompraItens(CompraItensRequestDTO data) {
        this.compraId = data.compraId();
        this.produtoId = data.produtoId();
        this.quantidade = data.quantidade();
        this.valorUnitario = data.valorUnitario();
        this.valorTotal = data.valorTotal();
        this.quantidadeRecebida = data.quantidadeRecebida();
        this.createdAt = data.createdAt();
    }
}
