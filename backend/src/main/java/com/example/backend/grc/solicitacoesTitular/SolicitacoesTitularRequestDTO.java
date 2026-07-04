package com.example.backend.grc.solicitacoesTitular;

import java.time.LocalDateTime;

public record SolicitacoesTitularRequestDTO(
        String titularNome,
        String titularContato,
        String tipoTitular,
        String direitoSolicitado,
        String modulo,
        String entidade,
        String status,
        String canalOrigem,
        String detalhes,
        LocalDateTime prazoResposta,
        LocalDateTime dataConclusao,
        String respostaResumo,
        Integer atendidoPor
) {
}
