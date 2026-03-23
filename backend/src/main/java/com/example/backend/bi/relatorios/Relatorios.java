package com.example.backend.bi.relatorios;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "relatorios", schema = "com/example/backend/bi")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Relatorios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;
    @Column(name = "tipo_relatorio")
    private String tipoRelatorio;
    @Column(name = "query_sql")
    private String querySql;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parametros",columnDefinition = "jsonb")
    private Map<String, Object> parametros;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Relatorios(RelatoriosRequestDTO data) {
        this.nome = data.nome();
        this.tipoRelatorio = data.tipoRelatorio();
        this.descricao = data.descricao();
        this.parametros = data.parametros();
        this.querySql = data.querySql();
        this.createdAt = data.createdAt();
    }
}
