package com.example.backend.core.empresas;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "empresas", schema = "core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Empresas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    @Column(name = "razao_social")
    private String razaoSocial;
    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    private String cnpj;
    @Column(name = "incricao_estadual")
    private String inscricaoEstadual;
    @Column(name = "inscricao_municipal")
    private String inscricaoMunicipal;
    @Column(name = "regime_tributario")
    private String regimeTributario;
    @Column(name = "data_fundacao")
    private LocalDate dataFundacao;
    private String situacao;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Empresas(EmpresasRequestDTO data){

        this.codigo = data.codigo();
        this.razaoSocial = data.razaoSocial();
        this.nomeFantasia = data.nomeFantasia();
        this.cnpj = data.cnpj();
        this.inscricaoEstadual = data.inscricaoEstadual();
        this.inscricaoMunicipal = data.inscricaoMunicipal();
        this.regimeTributario = data.regimeTributario();
        this.dataFundacao = data.dataFundacao();
        this.situacao = data.situacao();
        this.createdAt = data.createdAt();
    }
}
