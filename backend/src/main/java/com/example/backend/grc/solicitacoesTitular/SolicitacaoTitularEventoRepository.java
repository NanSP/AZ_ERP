package com.example.backend.grc.solicitacoesTitular;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoTitularEventoRepository extends JpaRepository<SolicitacaoTitularEvento, Integer> {
    List<SolicitacaoTitularEvento> findBySolicitacaoIdOrderByCreatedAtDesc(Integer solicitacaoId);
}
