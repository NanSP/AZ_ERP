package com.example.backend.fi.centrosCusto;

public record CentrosCustoRequestDTO
        (
                String codigo,
                String nome,
                String tipo,
                String responsavel,
                Boolean ativo
        ) {
}
