package com.example.backend.rh.dependentes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DependentesResponseDTO
        (
                Integer id,
                Integer colaborador,
                String nome,
                LocalDate dataNascimento,
                String parentesco,
                String cpf,
                LocalDateTime createdAt
        )
    {
        public DependentesResponseDTO(Dependentes dependentes){
            this
                    (
                       dependentes.getId(),
                       dependentes.getColaborador() != null ? dependentes.getColaborador().getId() : null,
                       dependentes.getNome(),
                       dependentes.getDataNascimento(),
                       dependentes.getParentesco(),
                       dependentes.getCpf(),
                       dependentes.getCreatedAt()
                    );
        }
}
