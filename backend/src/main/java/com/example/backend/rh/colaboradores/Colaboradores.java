package com.example.backend.rh.colaboradores;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "colaboradores", schema = "com/example/backend/rh")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Colaboradores {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String codigo;
    private String nome;
    private String cpf;
    private String rg;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String sexo;
    @Column(name = "estado_civil")
    private String estadoCivil;
    private String nacionalidade;
    @Column(name = "email_pessoal")
    private String emailPessoal;
    @Column(name = "email_corporativo")
    private String emailCorporativo;
    private String telefone;
    private String celular;
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;
    @Column(name = "data_demissao")
    private LocalDate dataDemissao;
    private String cargo;
    private String departamento;
    @Column(precision = 10, scale = 2)
    private BigDecimal salario;
    @Column(name = "tipo_contrato")
    private String tipoContrato;
    @Column(name = "jornada_semanal")
    private Integer jornadaSemanal;
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'ativo'")
    private String situacao = "ativo";
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Colaboradores(ColaboradoresRequestDTO data){
        this.codigo = data.codigo();
        this.nome = data.nome();
        this.cpf = data.cpf();
        this.rg = data.rg();
        this.dataNascimento = data.dataNascimento();
        this.sexo = data.sexo();
        this.estadoCivil = data.estadoCivil();
        this.nacionalidade = data.nacionalidade();
        this.emailPessoal = data.emailPessoal();
        this.emailCorporativo = data.emailCorporativo();
        this.telefone = data.telefone();
        this.celular = data.celular();
        this.dataAdmissao = data.dataAdmissao();
        this.dataDemissao = data.dataDemissao();
        this.cargo = data.cargo();
        this.departamento = data.departamento();
        this.salario = data.salario();
        this.tipoContrato = data.tipoContrato();
        this.jornadaSemanal = data.jornadaSemanal();
        this.situacao = data.situacao();
        this.createdAt = data.createdAt();
    }
}
