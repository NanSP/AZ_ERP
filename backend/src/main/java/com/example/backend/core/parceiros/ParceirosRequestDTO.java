package com.example.backend.core.parceiros;

import java.math.BigDecimal;

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
                String observacoes
        ) {
}
