package com.example.backend.mm.inventarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InventariosRequestDTO
        (
                LocalDate dataInicio,
                LocalDate dataFim,
                String tipoInventario,
                String status,
                String observacoes,
                LocalDateTime createdAt
        ) {
}
