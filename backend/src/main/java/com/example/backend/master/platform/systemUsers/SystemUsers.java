package com.example.backend.master.platform.systemUsers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "system_users", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SystemUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String login;

    @JsonIgnore
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

    public SystemUsers(SystemUsersRequestDTO data) {
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
