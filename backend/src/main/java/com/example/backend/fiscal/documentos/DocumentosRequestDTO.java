package com.example.backend.fiscal.documentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DocumentosRequestDTO
        (
                String tipoDocumento,
                String numero,
                String serie,
                String chaveAcesso,
                LocalDateTime dataEmissao,
                Integer pedido,
                Integer cliente,
                BigDecimal valorTotal,
                String status,
                String xml_file,
                LocalDateTime createdAt
        ) {
}
