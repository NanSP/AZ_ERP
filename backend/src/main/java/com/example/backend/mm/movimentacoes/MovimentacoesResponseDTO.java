package com.example.backend.mm.movimentacoes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacoesResponseDTO
        (
                Integer id,
                Integer estoque,
                String tipoMovimento,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                String documentoReferencia,
                String motivo,
                Integer usuario,
                LocalDateTime createdAt
        )
    {
        public MovimentacoesResponseDTO(Movimentacoes movimentacoes) {
            this
                    (
                            movimentacoes.getId(),
                            movimentacoes.getEstoque() != null ? movimentacoes.getEstoque().getId() : null,
                            movimentacoes.getTipoMovimento(),
                            movimentacoes.getQuantidade(),
                            movimentacoes.getValorUnitario(),
                            movimentacoes.getValorTotal(),
                            movimentacoes.getDocumentoReferencia(),
                            movimentacoes.getMotivo(),
                            movimentacoes.getUsuario() != null ? movimentacoes.getUsuario().getId() : null,
                            movimentacoes.getCreatedAt()
                    );
        }
}
