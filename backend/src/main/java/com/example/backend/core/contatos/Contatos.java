package com.example.backend.core.contatos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "contatos", schema = "com/example/backend/core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Contatos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entidade_tipo")
    private String entidadeTipo;
    @Column(name = "entidade_id")
    private Integer entidadeId;
    @Column(name = "tipo_contato")
    private String tipoContato;
    private String valor;
    private Boolean principal;
    private String observacao;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Contatos(ContatosRequestDTO data){
        this.entidadeTipo = data.entidadeTipo();
        this.entidadeId = data.entidadeId();
        this.tipoContato = data.tipoContato();
        this.valor = data.valor();
        this.principal = data.principal();
        this.observacao = data.observacao();
        this.createdAt = data.createdAt();
    }
}

