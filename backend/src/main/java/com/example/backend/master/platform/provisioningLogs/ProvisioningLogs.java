package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.tenants.Tenants;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "provisioning_logs", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProvisioningLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenants tenantId;

    private String etapa;
    private String status;
    private String mensagem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalhes",columnDefinition = "jsonb")
    private Map<String, Object> detalhes;

    @ManyToOne
    @JoinColumn(name = "executado_por")
    private SystemUsers executadoPor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ProvisioningLogs(ProvisioningLogsRequestDTO data) {
        this.etapa = data.etapa();
        this.status = data.status();
        this.mensagem = data.mensagem();
        this.detalhes = data.detalhes();
        this.createdAt = data.createdAt();
    }
}
