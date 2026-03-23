package com.example.backend.core.empresas;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmpresasResponseDTO
        (
                Integer id,
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

    public EmpresasResponseDTO(Empresas empresas) {
        this(
                empresas.getId(),
                empresas.getCodigo(),
                empresas.getRazaoSocial(),
                empresas.getNomeFantasia(),
                empresas.getCnpj(),
                empresas.getInscricaoEstadual(),
                empresas.getInscricaoMunicipal(),
                empresas.getRegimeTributario(),
                empresas.getDataFundacao(),
                empresas.getSituacao(),
                empresas.getCreatedAt()
        );
    }
}
