package rh.beneficios;

import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiosResponseDTO(
        Integer id,
        Colaboradores colaborador,
        String tipoBeneficio,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo) {

    public BeneficiosResponseDTO(Beneficios beneficios){
        this(
                beneficios.getId(),
                beneficios.getColaboradorId(),
                beneficios.getTipoBeneficio(),
                beneficios.getValor(),
                beneficios.getDataInicio(),
                beneficios.getDataFim(),
                beneficios.getAtivo()
        );
    }
}
