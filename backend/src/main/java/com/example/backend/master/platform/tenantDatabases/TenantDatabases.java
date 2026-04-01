package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.master.platform.tenants.Tenants;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TenantDatabases(TenantDatabasesRequestDTO data) {
        this.databaseName = data.databaseName();
        this.templateName = data.templateName();
        this.dbUsername = data.dbUsername();
        this.dbPort = data.dbPort();
        this.dbHost = data.dbHost();
        this.dbPassword = data.dbPassword();
        this.provisionStatus = data.provisionStatus();
        this.provisionedAt = data.provisionedAt();
        this.lastCheckAt = data.lastCheckAt();
        this.createdAt = data.createdAt();
        this.updatedAt = data.updatedAt();
    }

}
