package mm.movimentacoes;

import mm.estoques.Estoques;
import sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacoesRequestDTO
        (
                Estoques estoqueId,
                String tipoMovimento,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                String documentoReferencia,
                String motivo,
                Usuarios usuarioId,
                LocalDateTime createdAt
        ) {
}
