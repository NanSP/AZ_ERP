package com.example.backend.grc.shared;

import com.example.backend.shared.exception.ValidacaoException;

import java.util.Set;

public final class PrivacyRiskCatalog {

    private static final Set<String> HIGH_RISK = Set.of(
            "rh",
            "autenticacao",
            "sessoes",
            "dispositivos",
            "logs",
            "segredos_tecnicos"
    );

    private static final Set<String> MEDIUM_RISK = Set.of(
            "parceiros",
            "contatos",
            "enderecos",
            "clientes",
            "documentos_fiscais"
    );

    private static final Set<String> LOW_RISK = Set.of(
            "catalogo_produtos",
            "dados_nao_pessoais"
    );

    private PrivacyRiskCatalog() {
    }

    public static String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) {
            throw new ValidacaoException("Escopo critico e obrigatorio");
        }

        String normalized = scope.trim().toLowerCase();
        if (!HIGH_RISK.contains(normalized)
                && !MEDIUM_RISK.contains(normalized)
                && !LOW_RISK.contains(normalized)) {
            throw new ValidacaoException("Escopo critico invalido");
        }

        return normalized;
    }

    public static String derivePriority(String scope) {
        String normalized = normalizeScope(scope);
        if (HIGH_RISK.contains(normalized)) {
            return "alta";
        }

        if (MEDIUM_RISK.contains(normalized)) {
            return "media";
        }

        return "baixa";
    }
}
