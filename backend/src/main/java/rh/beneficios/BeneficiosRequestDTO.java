package rh.beneficios;

import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiosRequestDTO(
        Colaboradores colaboradorId,
        String tipoBeneficio,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo) {
}
