package sd.faturas;

import sd.pedidoItens.PedidoItens;
import sd.pedidos.Pedidos;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturasResponseDTO
        (
                Integer id,
                Pedidos pedidoId,
                String numeroFatura,
                LocalDate dataEmissao,
                BigInteger valorTotal,
                LocalDate dataVencimento,
                String status,
                LocalDateTime createdAt
        )
    {
        public FaturasResponseDTO(Faturas faturas) {
            this
                    (
                            faturas.getId(),
                            faturas.getPedidoId(),
                            faturas.getNumeroFatura(),
                            faturas.getDataEmissao(),
                            faturas.getValorTotal(),
                            faturas.getDataVencimento(),
                            faturas.getStatus(),
                            faturas.getCreatedAt()
                    );
        }
}
