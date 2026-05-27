package com.example.backend.mm.inventarios;

import java.time.LocalDate;

public record InventariosRequestDTO
        (
                LocalDate dataInicio,
                LocalDate dataFim,
                String tipoInventario,
                String status,
                String observacoes
        ) {
}
