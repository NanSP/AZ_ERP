package com.example.backend.grc.registrosTratamento;

public record RegistrosTratamentoRequestDTO(
        String modulo,
        String entidade,
        String finalidade,
        String baseLegal,
        String categoriaTitular,
        String categoriaDados,
        Integer retencaoDias,
        String compartilhamento,
        Boolean requerConsentimento,
        Boolean ativo,
        Integer responsavel,
        String observacoes
) {
}
