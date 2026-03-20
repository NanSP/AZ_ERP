package rh.dependentes;

import rh.colaboradores.Colaboradores;

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
