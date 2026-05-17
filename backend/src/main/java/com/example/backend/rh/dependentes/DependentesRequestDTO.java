package com.example.backend.rh.dependentes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DependentesRequestDTO
        (
                Integer colaborador,
                String nome,
                LocalDate dataNascimento,
                String parentesco,
                String cpf,
                LocalDateTime createdAt
        ) {
}
