package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratosResponseDTO
        (
                Integer id,
                Parceiros clienteId,
                String numeroContrato,
                String objeto,
                BigInteger valorTotal,
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
                            contratos.getClienteId(),
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
