package core.parceiros;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParceirosRequestDTO
        (
                String tipoParceiro,
                String codigo,
                String nome,
                String nomeFantasia,
                String documento,
                String tipoPessoa,
                String situacao,
                BigDecimal limiteCredito,
                Integer diasPrazo,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
