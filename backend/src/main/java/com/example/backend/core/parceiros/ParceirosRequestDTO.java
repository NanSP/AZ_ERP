package com.example.backend.core.parceiros;

import com.example.backend.fi.contasPagar.ContasPagar;
import com.example.backend.fi.contasReceber.ContasReceber;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ParceirosRequestDTO
        (
                String tipoParceiro,
                String codigo,
                String nome,
                String nomeFantasia,
                String documento,
                String tipoPessoa,
                String situacao,
                BigDecimal limiteCredito,
                Integer diasPrazo,
                String observacoes,
                List<ContasPagar> contasAPagar,
                List<ContasReceber> contasAReceber,
                LocalDateTime createdAt
        ) {
}
