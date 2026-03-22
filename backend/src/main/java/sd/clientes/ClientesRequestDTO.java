package sd.clientes;

import core.parceiros.Parceiros;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientesRequestDTO
        (
                Parceiros parceiroId,
                String classificacao,
                String origem,
                String website,
                BigDecimal faturamentoAnual,
                Integer numeroFuncionarios,
                LocalDateTime createdAt
        ) {
}
