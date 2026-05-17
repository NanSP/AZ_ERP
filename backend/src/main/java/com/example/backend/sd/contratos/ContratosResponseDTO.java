package com.example.backend.sd.contratos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratosResponseDTO
        (
                Integer id,
                Integer cliente,
                String numeroContrato,
                String objeto,
                BigDecimal valorTotal,
                LocalDate dataInicio,
                LocalDate dataFim,
                String status,
                LocalDateTime createdAt
        )
    {
        public ContratosResponseDTO(Contratos contratos) {
            this
                    (
                            contratos.getId(),
                            contratos.getCliente() != null ? contratos.getCliente().getId() : null,
                            contratos.getNumeroContrato(),
                            contratos.getObjeto(),
                            contratos.getValorTotal(),
                            contratos.getDataInicio(),
                            contratos.getDataFim(),
                            contratos.getStatus(),
                            contratos.getCreatedAt()
                    );
        }
}
