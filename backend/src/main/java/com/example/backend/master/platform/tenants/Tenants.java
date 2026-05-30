package com.example.backend.master.platform.tenants;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
