package com.example.backend.core.enderecos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "enderecos", schema = "core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Enderecos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entidade_tipo")
    private String entidadeTipo;
    @Column(name = "entidade_id")
    private Integer entidadeId;
    @Column(name = "tipo_endereco")
    private String tipoEndereco;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String pais;
    private Boolean principal;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
