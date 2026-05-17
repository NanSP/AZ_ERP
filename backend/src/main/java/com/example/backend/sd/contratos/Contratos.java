package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "contratos", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Contratos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Parceiros cliente;
    @Column(name = "numero_contrato")
    private String numeroContrato;
    private String objeto;
    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
