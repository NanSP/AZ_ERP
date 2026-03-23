package fi.planoContas;

import java.util.List;

public record PlanoContasRequestDTO
        (
                String codigo,
                String nome,
                String tipoConta,
                String natureza,
                PlanoContas planoContas,
                String situacao,
                List<PlanoContas> contasFilhas
        ) {
}
