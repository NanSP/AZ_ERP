package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "solicitacao_titular_eventos", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitacaoTitularEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitacao_id")
    private SolicitacoesTitular solicitacao;

    @Column(name = "tipo_evento")
    private String tipoEvento;

    private String titulo;
    private String descricao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalhes_json", columnDefinition = "jsonb")
    private Map<String, Object> detalhesJson;

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private Usuarios criadoPor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
