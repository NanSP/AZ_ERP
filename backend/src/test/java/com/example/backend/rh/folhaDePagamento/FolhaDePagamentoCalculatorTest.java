package com.example.backend.rh.folhaDePagamento;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FolhaDePagamentoCalculatorTest {

    private final FolhaDePagamentoCalculator calculator = new FolhaDePagamentoCalculator();

    @Test
    void deveCalcularFolhaComHorasExtrasAdicionaisEDescontos() {
        FolhaCalculadaDTO calculada = calculator.calcular(
                new BigDecimal("2200.00"),
                new BigDecimal("160.00"),
                new BigDecimal("10.00"),
                new BigDecimal("300.00"),
                new BigDecimal("150.00")
        );

        assertEquals(new BigDecimal("10.00"), calculada.valorHora());
        assertEquals(new BigDecimal("1600.00"), calculada.valorHorasNormais());
        assertEquals(new BigDecimal("150.00"), calculada.valorHorasExtras());
        assertEquals(new BigDecimal("2050.00"), calculada.valorBruto());
        assertEquals(new BigDecimal("1900.00"), calculada.valorLiquido());
    }

    @Test
    void deveTratarValoresNulosComoZero() {
        FolhaCalculadaDTO calculada = calculator.calcular(
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(new BigDecimal("0.00"), calculada.valorHora());
        assertEquals(new BigDecimal("0.00"), calculada.valorHorasNormais());
        assertEquals(new BigDecimal("0.00"), calculada.valorHorasExtras());
        assertEquals(new BigDecimal("0.00"), calculada.valorBruto());
        assertEquals(new BigDecimal("0.00"), calculada.valorLiquido());
    }
}
