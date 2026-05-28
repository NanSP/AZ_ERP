package com.example.backend.fiscal.documentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentosResponseDTO
        (
                Integer id,
                String tipoDocumento,
                String numero,
                String serie,
                String chaveAcesso,
                LocalDate dataEmissao,
                Integer pedido,
                Integer cliente,
                BigDecimal valorTotal,
                String status,
                String xml_file,
                LocalDateTime createdAt
        ) {
    public DocumentosResponseDTO(Documentos documentos) {
        this
                (
                        documentos.getId(),
                        documentos.getTipoDocumento(),
                        documentos.getNumero(),
                        documentos.getSerie(),
                        documentos.getChaveAcesso(),
                        documentos.getDataEmissao(),
                        documentos.getPedido() != null ? documentos.getPedido().getId() : null,
                        documentos.getCliente() != null ? documentos.getCliente().getId() : null,
                        documentos.getValorTotal(),
                        documentos.getStatus(),
                        documentos.getXml_file(),
                        documentos.getCreatedAt()
                );
    }
}
