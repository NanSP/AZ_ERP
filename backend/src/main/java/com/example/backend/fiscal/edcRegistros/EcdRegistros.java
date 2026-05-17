package com.example.backend.fiscal.edcRegistros;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "ecd_registros", schema = "sped")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class EcdRegistros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate periodo;
    private String registro;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conteudo",columnDefinition = "jsonb")
    private Map<String, Object> conteudo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
