package com.example.backend.core.empresas;

import java.time.LocalDate;

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
                String situacao
        ) {
}
