package com.example.backend.fi.planoContas;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "plano_contas", schema = "contabil")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PlanoContas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String nome;
    @Column(name = "tipo_conta")
    private String tipoConta;
    private String natureza;
    @ManyToOne
    @JoinColumn(name = "conta_pai_id")
    private PlanoContas contaPai;
    private String situacao;

    @OneToMany(mappedBy = "contaPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoContas> contasFilhas = new ArrayList<>();


}
