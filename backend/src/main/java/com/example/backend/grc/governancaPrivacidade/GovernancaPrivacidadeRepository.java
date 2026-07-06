package com.example.backend.grc.governancaPrivacidade;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GovernancaPrivacidadeRepository extends JpaRepository<GovernancaPrivacidade, Integer> {
    boolean existsByNomeReferenciaIgnoreCase(String nomeReferencia);
    boolean existsByNomeReferenciaIgnoreCaseAndIdNot(String nomeReferencia, Integer id);
    boolean existsByAtivoTrue();
    boolean existsByAtivoTrueAndIdNot(Integer id);
}
