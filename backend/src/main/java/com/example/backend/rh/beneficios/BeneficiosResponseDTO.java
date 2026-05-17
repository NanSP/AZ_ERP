package com.example.backend.rh.beneficios;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiosResponseDTO(
        Integer id,
        Integer colaborador,
        String tipoBeneficio,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo) {

    public BeneficiosResponseDTO(Beneficios beneficios){
        this(
                beneficios.getId(),
                beneficios.getColaborador() != null ? beneficios.getColaborador().getId() : null,
                beneficios.getTipoBeneficio(),
                beneficios.getValor(),
                beneficios.getDataInicio(),
                beneficios.getDataFim(),
                beneficios.getAtivo()
        );
    }
}
