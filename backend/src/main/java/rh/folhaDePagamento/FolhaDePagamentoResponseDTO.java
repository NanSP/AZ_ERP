package rh.folhaDePagamento;

import rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FolhaDePagamentoResponseDTO
        (
                Integer id,
                Colaboradores colaboradorId,
                LocalDate competencia,
                BigDecimal salarioBase,
                BigDecimal horasNormais,
                BigDecimal horasExtras,
                BigDecimal adicionais,
                BigDecimal descontos,
                BigDecimal valorLiquido,
                LocalDate dataPagamento,
                String status,
                LocalDateTime createdAt
        )
    {
        public FolhaDePagamentoResponseDTO(FolhaDePagamento folhaDePagamento){
            this(
                    folhaDePagamento.getId(),
                    folhaDePagamento.getColaboradorId(),
                    folhaDePagamento.getCompetencia(),
                    folhaDePagamento.getSalarioBase(),
                    folhaDePagamento.getHorasNormais(),
                    folhaDePagamento.getHorasExtras(),
                    folhaDePagamento.getAdicionais(),
                    folhaDePagamento.getDescontos(),
                    folhaDePagamento.getValorLiquido(),
                    folhaDePagamento.getDataPagamento(),
                    folhaDePagamento.getStatus(),
                    folhaDePagamento.getCreatedAt()
            );
        }
}
