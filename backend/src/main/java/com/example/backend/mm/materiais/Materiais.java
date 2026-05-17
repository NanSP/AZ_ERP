package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "materiais", schema = "materiais")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Materiais {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;
    @Column(name = "tipo_material")
    private String tipoMaterial;
    private String categoria;
    private String subcategoria;
    private String marca;
    private String modelo;
    @Column(name = "especificacoes_tecnicas")
    private String especificacoesTecnicas;
    @Column(name = "condicao_armazenamento")
    private String condicaoArmazenamento;
    @Column(name = "classe_perigo")
    private String classePerigo;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
