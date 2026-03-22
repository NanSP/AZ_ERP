package fi.movimentacoesBancarias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentacoesBancariasRequestDTO
        (
                Integer contaBancariaId,
                String tipoMovimento,
                BigDecimal valor,
                LocalDate dataMovimento,
                String historico,
                String documentoVinculado,
                Boolean conciliado,
                LocalDate dataConciliacao,
                LocalDateTime createdAt
        ) {
}
