package com.example.backend.core.parceiros;

import com.example.backend.fi.contasPagar.ContasPagar;
import com.example.backend.fi.contasReceber.ContasReceber;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ParceirosResponseDTO
        (
                Integer id,
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
        )
    {
        public ParceirosResponseDTO(Parceiros parceiros) {
            this
                    (
                            parceiros.getId(),
                            parceiros.getTipoParceiro(),
                            parceiros.getCodigo(),
                            parceiros.getNome(),
                            parceiros.getNomeFantasia(),
                            parceiros.getDocumento(),
                            parceiros.getTipoPessoa(),
                            parceiros.getSituacao(),
                            parceiros.getLimiteCredito(),
                            parceiros.getDiasPrazo(),
                            parceiros.getObservacoes(),
                            parceiros.getContasAPagar(),
                            parceiros.getContasAReceber(),
                            parceiros.getCreatedAt()

                    );
        }
}
