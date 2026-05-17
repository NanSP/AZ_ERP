package com.example.backend.qm.inspecoes;

import com.example.backend.core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.rh.colaboradores.Colaboradores;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "inspecoes", schema = "qualidade")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Inspecoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_inspecao")
    private String tipoInspecao;
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    private String lote;
    @Column(name = "quantidade_inspecionada", precision = 15, scale = 4)
    private BigDecimal quantidadeInspecionada;
    @Column(name = "quantidade_aprovada", precision = 15, scale = 4)
    private BigDecimal quantidadeAprovada;
    @Column(name = "quantidade_reprovada", precision = 15, scale = 4)
    private BigDecimal quantidadeReprovada;
    @Column(name = "data_inspecao")
    private LocalDate dataInspecao;

    @ManyToOne
    @JoinColumn(name = "inspetor_id")
    private Colaboradores inspetor;

    private String resultado;
    private String observacoes;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
