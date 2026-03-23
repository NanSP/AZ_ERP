package com.example.backend.mm.movimentacoes;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.mm.estoques.Estoques;
import com.example.backend.sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "movimentacoes", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Movimentacoes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "estoque_id")
    private Estoques estoqueId;
    @Column(name = "tipo_movimento")
    private String tipoMovimento;
    @Column(precision = 10, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "valor_unitario", precision = 10, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "valor_total", precision = 10, scale = 4)
    private BigDecimal valorTotal;
    @Column(name = "documento_referencia")
    private String documentoReferencia;
    private String motivo;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Movimentacoes(MovimentacoesRequestDTO data) {
        this.tipoMovimento = data.tipoMovimento();
        this.quantidade = data.quantidade();
        this.valorUnitario = data.valorUnitario();
        this.valorTotal = data.valorTotal();
        this.documentoReferencia = data.documentoReferencia();
        this.usuarioId = data.usuarioId();
        this.motivo = data.motivo();
        this.createdAt = data.createdAt();
    }
}
