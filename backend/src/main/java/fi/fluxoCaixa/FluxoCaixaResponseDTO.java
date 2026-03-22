package fi.fluxoCaixa;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FluxoCaixaResponseDTO
        (
                Integer id,
                LocalDate dataReferencia,
                BigDecimal saldoInicial,
                BigDecimal entradasPrevistas,
                BigDecimal saidasPrevistas,
                BigDecimal entradasRealizadas,
                BigDecimal saidasRealizadas,
                BigDecimal saldoFinalPrevisto,
                BigDecimal saldoFinalReal,
                LocalDateTime createdAt
        )
    {
        public FluxoCaixaResponseDTO(FluxoCaixa fluxoCaixa) {
            this
                    (
                            fluxoCaixa.getId(),
                            fluxoCaixa.getDataReferencia(),
                            fluxoCaixa.getSaldoInicial(),
                            fluxoCaixa.getEntradasPrevistas(),
                            fluxoCaixa.getSaidasPrevistas(),
                            fluxoCaixa.getEntradasRealizadas(),
                            fluxoCaixa.getSaidasRealizadas(),
                            fluxoCaixa.getSaldoFinalPrevisto(),
                            fluxoCaixa.getSaldoFinalReal(),
                            fluxoCaixa.getCreatedAt()
                    );
        }
}
