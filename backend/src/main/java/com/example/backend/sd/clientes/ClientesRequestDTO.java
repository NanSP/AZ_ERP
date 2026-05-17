package com.example.backend.sd.clientes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientesRequestDTO
        (
                Integer parceiro,
                String classificacao,
                String origem,
                String website,
                BigDecimal faturamentoAnual,
                Integer numeroFuncionarios,
                LocalDateTime createdAt
        ) {
}
