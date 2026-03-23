package com.example.backend.fiscal.documentos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DocumentosRequestDTO
        (
                String tipoDocumento,
                String numero,
                String serie,
                String chaveAcesso,
                LocalDateTime dataEmissao,
                Pedidos pedidoId,
                Parceiros clienteId,
                BigDecimal valorTotal,
                String status,
                String xml_file,
                LocalDateTime createdAt
        ) {
}
