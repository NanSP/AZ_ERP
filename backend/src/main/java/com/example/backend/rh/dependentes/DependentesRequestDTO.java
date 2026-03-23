package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DependentesRequestDTO
        (
                Colaboradores colaboradorId,
                String nome,
                LocalDate dataNascimento,
                String parentesco,
                String cpf,
                LocalDateTime createdAt
        ) {
}
