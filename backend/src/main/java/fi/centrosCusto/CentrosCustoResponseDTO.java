package fi.centrosCusto;

public record CentrosCustoResponseDTO
        (
                Integer id,
                String codigo,
                String nome,
                String tipo,
                String responsavel,
                Boolean ativo
        )
    {
        public CentrosCustoResponseDTO(CentrosCusto centrosCusto) {
            this
                    (
                            centrosCusto.getId(),
                            centrosCusto.getCodigo(),
                            centrosCusto.getNome(),
                            centrosCusto.getTipo(),
                            centrosCusto.getResponsavel(),
                            centrosCusto.getAtivo()
                    );
        }
}
