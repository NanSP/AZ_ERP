package com.example.backend.fi.movimentacoesBancarias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentacoesBancariasResponseDTO
        (
                Integer id,
                Integer contaBancariaId,
                String tipoMovimento,
                BigDecimal valor,
                LocalDate dataMovimento,
                String historico,
                String documentoVinculado,
                Boolean conciliado,
                LocalDate dataConciliacao,
                LocalDateTime createdAt
        )
    {
        public MovimentacoesBancariasResponseDTO(MovimentacoesBancarias movimentacoesBancarias) {
            this
                    (
                            movimentacoesBancarias.getId(),
                            movimentacoesBancarias.getContaBancariaId(),
                            movimentacoesBancarias.getTipoMovimento(),
                            movimentacoesBancarias.getValor(),
                            movimentacoesBancarias.getDataMovimento(),
                            movimentacoesBancarias.getHistorico(),
                            movimentacoesBancarias.getDocumentoVinculado(),
                            movimentacoesBancarias.getConciliado(),
                            movimentacoesBancarias.getDataConciliacao(),
                            movimentacoesBancarias.getCreatedAt()
                    );
        }
}
