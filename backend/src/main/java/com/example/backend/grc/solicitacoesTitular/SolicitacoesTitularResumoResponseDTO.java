package com.example.backend.grc.solicitacoesTitular;

public record SolicitacoesTitularResumoResponseDTO(
        long abertas,
        long emAnalise,
        long aguardandoTitular,
        long concluidas,
        long indeferidas,
        long vencidas,
        long vencendoEmBreve
) {
}
