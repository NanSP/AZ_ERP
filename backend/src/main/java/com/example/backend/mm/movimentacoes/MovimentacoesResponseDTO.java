package com.example.backend.mm.movimentacoes;

import com.example.backend.mm.estoques.Estoques;
import com.example.backend.sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacoesResponseDTO
        (
                Integer id,
                Estoques estoqueId,
                String tipoMovimento,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                String documentoReferencia,
                String motivo,
                Usuarios usuarioId,
                LocalDateTime createdAt
        )
    {
        public MovimentacoesResponseDTO(Movimentacoes movimentacoes) {
            this
                    (
                            movimentacoes.getId(),
                            movimentacoes.getEstoqueId(),
                            movimentacoes.getTipoMovimento(),
                            movimentacoes.getQuantidade(),
                            movimentacoes.getValorUnitario(),
                            movimentacoes.getValorTotal(),
                            movimentacoes.getDocumentoReferencia(),
                            movimentacoes.getMotivo(),
                            movimentacoes.getUsuarioId(),
                            movimentacoes.getCreatedAt()
                    );
        }
}
