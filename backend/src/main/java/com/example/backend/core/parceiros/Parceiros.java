package com.example.backend.core.parceiros;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "parceiros", schema = "core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Parceiros {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_parceiro")
    private String tipoParceiro;
    private String codigo;
    private String nome;
    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    private String documento;
    @Column(name = "tipo_pessoa")
    private String tipoPessoa;
    private String situacao;
    @Column(name = "limite_credito", precision = 10, scale = 2)
    private BigDecimal limiteCredito;
    @Column(name = "dias_prazo")
    private Integer diasPrazo;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
