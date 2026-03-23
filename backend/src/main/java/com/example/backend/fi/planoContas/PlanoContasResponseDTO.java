package com.example.backend.fi.planoContas;

import java.util.List;

public record PlanoContasResponseDTO
        (
                Integer id,
                String codigo,
                String nome,
                String tipoConta,
                String natureza,
                PlanoContas planoContas,
                String situacao,
                List<PlanoContas> contasFilhas
        )
    {
        public PlanoContasResponseDTO(PlanoContas planoContas) {
            this
                    (
                            planoContas.getId(),
                            planoContas.getCodigo(),
                            planoContas.getNome(),
                            planoContas.getTipoConta(),
                            planoContas.getNatureza(),
                            planoContas.getPlanoContas(),
                            planoContas.getSituacao(),
                            planoContas.getContasFilhas()
                    );
        }
}
