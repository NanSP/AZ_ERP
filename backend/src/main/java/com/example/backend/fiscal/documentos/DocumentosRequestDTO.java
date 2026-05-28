package com.example.backend.fiscal.documentos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DocumentosRequestDTO
        (
                String tipoDocumento,
                String numero,
                String serie,
                String chaveAcesso,
                LocalDate dataEmissao,
                Integer pedido,
                Integer cliente,
                BigDecimal valorTotal,
                String status,
                String xml_file
        ) {
}
