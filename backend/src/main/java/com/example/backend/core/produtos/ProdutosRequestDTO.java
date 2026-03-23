package com.example.backend.core.produtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutosRequestDTO
        (
                String codigo,
                String codigoBarras,
                String nome,
                String descricao,
                String tipoItem,
                String unidadeMedida,
                String ncm,
                String cest,
                BigDecimal pesoBruto,
                BigDecimal pesoLiquido,
                Integer origem,
                String situacao,
                LocalDateTime createdAt
        ) {
}
