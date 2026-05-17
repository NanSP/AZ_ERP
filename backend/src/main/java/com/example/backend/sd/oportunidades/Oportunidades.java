package com.example.backend.sd.oportunidades;

import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;
import com.example.backend.sd.clientes.Clientes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "oportunidades", schema = "crm")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Oportunidades {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;
    private String titulo;
    private String descricao;
    @Column(name = "valor_estimado", precision = 15, scale = 2)
    private BigDecimal valorEstimado;
    private Integer probabilidade;
    private String estagio;
    @Column(name = "data_prevista_fechamento")
    private LocalDate dataPrevistaFechamento;
    @Column(name = "motivo_perda")
    private String motivoPerda;
    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
