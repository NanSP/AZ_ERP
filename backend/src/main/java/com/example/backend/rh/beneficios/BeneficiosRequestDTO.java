package com.example.backend.rh.beneficios;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiosRequestDTO(
        Integer colaborador,
        String tipoBeneficio,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo) {
}
