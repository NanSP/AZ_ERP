package rh.dependentes;

import rh.colaboradores.Colaboradores;
import rh.controleDePonto.ControleDePonto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DependentesResponseDTO
        (
                Integer id,
                Colaboradores colaboradorId,
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
                       dependentes.getColaboradorId(),
                       dependentes.getNome(),
                       dependentes.getDataNascimento(),
                       dependentes.getParentesco(),
                       dependentes.getCpf(),
                       dependentes.getCreatedAt()
                    );
        }
}
