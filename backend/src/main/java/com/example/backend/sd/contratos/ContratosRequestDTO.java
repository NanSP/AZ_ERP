package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratosRequestDTO
        (
                Parceiros clienteId,
                String numeroContrato,
                String objeto,
                BigInteger valorTotal,
                LocalDate dataInicio,
                LocalDate dataFim,
                String status,
                LocalDateTime createdAt
        ) {
}
