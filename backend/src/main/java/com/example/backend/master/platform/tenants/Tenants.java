package com.example.backend.master.platform.tenants;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "tenants", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tenants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nome;
    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    private String documento;
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    @Column(name = "email_responsavel")
    private String emailResponsavel;
    @Column(name = "telefone_responsavel")
    private String telefoneResponsavel;
    private String status;
    private String plano;
    @Column(name = "schema_version")
    private String schemaVersion;
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Tenants(TenantsRequestDTO data) {
        this.codigo = data.codigo();
        this.nome = data.nome();
        this.nomeFantasia = data.nomeFantasia();
        this.documento = data.documento();
        this.tipoDocumento = data.tipoDocumento();
        this.emailResponsavel = data.emailResponsavel();
        this.telefoneResponsavel = data.telefoneResponsavel();
        this.plano = data.plano();
        this.status = data.status();
        this.schemaVersion = data.schemaVersion();
        this.observacoes = data.observacoes();
        this.createdAt = data.createdAt();
        this.updatedAt = data.updatedAt();
    }
}
