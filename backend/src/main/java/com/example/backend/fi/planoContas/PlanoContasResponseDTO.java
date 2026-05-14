package com.example.backend.fi.planoContas;

public record PlanoContasResponseDTO
        (
                Integer id,
                String codigo,
                String nome,
                String tipoConta,
                String natureza,
                Integer contaPai,
                String situacao

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
                            planoContas.getContaPai()!= null ? planoContas.getContaPai().getId() : null,
                            planoContas.getSituacao()

                    );
        }
}
