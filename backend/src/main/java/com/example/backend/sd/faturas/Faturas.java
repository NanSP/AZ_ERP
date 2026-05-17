package com.example.backend.sd.faturas;

import com.example.backend.sd.pedidos.Pedidos;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "faturas", schema = "vendas")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Faturas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;
    @Column(name = "numero_fatura")
    private String numeroFatura;
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
