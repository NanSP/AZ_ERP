package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "dependetes", schema = "rh")
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
    private Colaboradores colaboradorId;

    private String nome;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String parentesco;
    private String cpf;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Dependentes(DependentesRequestDTO data){
        this.colaboradorId = data.colaboradorId();
        this.nome = data.nome();
        this.dataNascimento = data.dataNascimento();
        this.parentesco = data.parentesco();
        this.cpf = data.cpf();
        this.createdAt = data.createdAt();
    }
}
