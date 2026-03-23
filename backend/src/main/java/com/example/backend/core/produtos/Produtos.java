package com.example.backend.core.produtos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "produtos", schema = "com/example/backend/core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produtos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String codigo;
    @Column(name = "codigo_barras")
    private String codigoBarras;
    private String nome;
    private String descricao;
    @Column(name = "tipo_item")
    private String tipoItem;
    @Column(name = "unidade_medida")
    private String unidadeMedida;
    private String ncm;
    private String cest;
    @Column(name = "peso_bruto", precision = 10, scale = 4)
    private BigDecimal pesoBruto;
    @Column(name = "peso_liquido", precision = 10, scale = 4)
    private BigDecimal pesoLiquido;
    private Integer origem;
    private String situacao;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Produtos(ProdutosRequestDTO data){

        this.codigo = data.codigo();
        this.codigoBarras = data.codigoBarras();
        this.nome = data.nome();
        this.descricao = data.descricao();
        this.tipoItem = data.tipoItem();
        this.unidadeMedida = data.unidadeMedida();
        this.ncm = data.ncm();
        this.cest = data.cest();
        this.pesoBruto = data.pesoBruto();
        this.pesoLiquido = data.pesoLiquido();
        this.origem = data.origem();
        this.situacao = data.situacao();
        this.createdAt = data.createdAt();
    }
}
