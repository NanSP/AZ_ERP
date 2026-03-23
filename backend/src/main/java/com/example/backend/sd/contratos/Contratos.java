package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigInteger;
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
    private Parceiros clienteId;
    @Column(name = "numero_contrato")
    private String numeroContrato;
    private String objeto;
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigInteger valorTotal;
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    @Column(name = "data_fim")
    private LocalDate dataFim;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Contratos(ContratosRequestDTO data) {
        this.clienteId = data.clienteId();
        this.numeroContrato = data.numeroContrato();
        this.objeto = data.objeto();
        this.valorTotal = data.valorTotal();
        this.dataInicio = data.dataInicio();
        this.dataFim = data.dataFim();
        this.status = data.status();
        this.createdAt = data.createdAt();
    }
}
