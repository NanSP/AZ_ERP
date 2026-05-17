package com.example.backend.mm.estoques;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.mm.movimentacoes.Movimentacoes;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "estoques", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Estoques {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresas empresa;

    private String localizacao;
    private String lote;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;
    @Column(name = "quantidade_minima", precision = 15, scale = 4)
    private BigDecimal quantidadeMinima;
    @Column(name = "quantidade_maxima", precision = 15, scale = 4)
    private BigDecimal quantidadeMaxima;
    @Column(name = "valor_unitario", precision = 15, scale = 4)
    private BigDecimal valorUnitario;
    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimentacoes> movimentacoes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
