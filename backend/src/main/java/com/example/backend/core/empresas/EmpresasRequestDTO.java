package com.example.backend.core.empresas;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmpresasRequestDTO
        (
                String codigo,
                String razaoSocial,
                String nomeFantasia,
                String cnpj,
                String inscricaoEstadual,
                String inscricaoMunicipal,
                String regimeTributario,
                LocalDate dataFundacao,
                String situacao,
                LocalDateTime createdAt
        ) {
}
