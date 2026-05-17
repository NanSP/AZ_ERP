package com.example.backend.sd.clientes;

import com.example.backend.core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "clientes", schema = "crm")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Clientes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parceiro_id")
    private Parceiros parceiro;
    private String classificacao;
    private String origem;
    private String website;
    @Column(name = "faturamento_anual", precision = 15, scale = 2)
    private BigDecimal faturamentoAnual;
    @Column(name = "numero_funcionarios")
    private Integer numeroFuncionarios;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
