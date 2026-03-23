package com.example.backend.rh.colaboradores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ColaboradoresRequestDTO
        (
                String codigo,
                String nome,
                String cpf,
                String rg,
                LocalDate dataNascimento,
                String sexo,
                String estadoCivil,
                String nacionalidade,
                String emailPessoal,
                String emailCorporativo,
                String telefone,
                String celular,
                LocalDate dataAdmissao,
                LocalDate dataDemissao,
                String cargo,
                String departamento,
                BigDecimal salario,
                String tipoContrato,
                Integer jornadaSemanal,
                String situacao,
                LocalDateTime createdAt
        )
    {
}
