package fi.planoContas;

public record PlanoContasRequestDTO
        (
                String codigo,
                String nome,
                String tipoConta,
                String natureza,
                PlanoContas planoContas,
                String situacao
        ) {
}
