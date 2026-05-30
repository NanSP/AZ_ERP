package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "tenant_databases", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TenantDatabases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private Tenants tenantId;
    @Column(name = "database_name")
    private String databaseName;
    @Column(name = "template_name")
    private String templateName;
    @Column(name = "db_host")
    private String dbHost;
    @Column(name = "db_port")
    private Integer dbPort;
    @Column(name = "db_username")
    private String dbUsername;
    @Column(name = "db_password_encrypted")
    private String dbPassword;
    @Column(name = "provisioned_at")
    private LocalDateTime provisionedAt;
    @Column(name = "provision_status")
    private String provisionStatus;
    @Column(name = "last_check_at")
    private LocalDateTime lastCheckAt;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
