package com.example.backend.bi.dashboards;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "dashboards", schema = "bi")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Dashboards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "layout",columnDefinition = "jsonb")
    private Map<String, Object> layout;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracoes",columnDefinition = "jsonb")
    private Map<String, Object> configuracoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Dashboards(DashboardsRequestDTO data) {
        this.nome = data.nome();
        this.descricao = data.descricao();
        this.layout = data.layout();
        this.configuracoes = data.configuracoes();
        this.createdAt = data.createdAt();
    }
}
