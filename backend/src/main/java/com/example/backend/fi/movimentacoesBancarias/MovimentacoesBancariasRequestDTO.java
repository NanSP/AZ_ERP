package com.example.backend.fi.movimentacoesBancarias;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacoesBancariasRequestDTO
        (
                Integer contaBancariaId,
                String tipoMovimento,
                BigDecimal valor,
                LocalDate dataMovimento,
                String historico,
                String documentoVinculado,
                Boolean conciliado,
                LocalDate dataConciliacao
        ) {
}
