package com.example.backend.grc.governancaPrivacidade;

import java.time.LocalDateTime;

public record GovernancaPrivacidadeRequestDTO(
        String nomeReferencia,
        String papelPrivacidade,
        String encarregadoNome,
        String encarregadoEmail,
        String encarregadoCanal,
        String baseContratual,
        String clausulasContratuais,
        Boolean suboperadoresDeclarados,
        Boolean transferenciaInternacional,
        String procedimentoIncidente,
        Boolean ativo,
        LocalDateTime vigenteDesde,
        LocalDateTime revisaoProgramadaEm,
        String observacoes
) {
}
