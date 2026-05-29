package com.example.backend.core.parceiros;

import com.example.backend.fi.contasPagar.ContasPagar;
import com.example.backend.fi.contasReceber.ContasReceber;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "fornecedor")
    private List<ContasPagar> contasAPagar = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<ContasReceber> contasAReceber = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
