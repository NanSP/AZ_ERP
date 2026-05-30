package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "tenant_admin_users", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TenantAdminUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenants tenantId;

    private String nome;
    private String email;
    private String login;

    @JsonIgnore
    @Column(name = "senha_hash")
    private String senha;
    private String role;
    private String status;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
