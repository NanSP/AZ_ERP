package com.example.backend.grc.consentimentos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConsentimentosRepository extends JpaRepository<Consentimentos, Integer> {
    boolean existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNull(
            Integer titular,
            String tipoTitular,
            String finalidade
    );

    boolean existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNullAndIdNot(
            Integer titular,
            String tipoTitular,
            String finalidade,
            Integer id
    );

    boolean existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNullAndDataConsentimentoLessThan(
            Integer titular,
            String tipoTitular,
            String finalidade,
            LocalDateTime dataConsentimento
    );
}
