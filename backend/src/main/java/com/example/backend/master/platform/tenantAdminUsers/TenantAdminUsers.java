package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.master.platform.tenants.Tenants;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenants tenantId;

    private String nome;
    private String email;
    private String login;

    @Column(name = "senha_hash")
    private String senhaHash;
    private String role;
    private String status;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TenantAdminUsers(TenantAdminUsersRequestDTO data) {
        this.tenantId = data.tenantId();
        this.nome = data.nome();
        this.email = data.email();
        this.login = data.login();
        this.senhaHash = data.senhaHash();
        this.role = data.role();
        this.status = data.status();
        this.ultimoAcesso = data.ultimoAcesso();
        this.createdAt = data.createdAt();
        this.updatedAt = data.updatedAt();
    }
}
