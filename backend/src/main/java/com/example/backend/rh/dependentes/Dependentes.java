package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "dependentes", schema = "rh")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Dependentes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaboradores colaborador;

    private String nome;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String parentesco;
    private String cpf;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
