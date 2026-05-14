package com.example.backend.fi.planoContas;

public record PlanoContasRequestDTO
        (
                String codigo,
                String nome,
                String tipoConta,
                String natureza,
                Integer contaPai,
                String situacao
        ) {
}
