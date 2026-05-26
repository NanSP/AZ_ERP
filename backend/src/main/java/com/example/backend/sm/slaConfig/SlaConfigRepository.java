package com.example.backend.sm.slaConfig;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaConfigRepository extends JpaRepository<SlaConfig, Integer> {
    boolean existsByTipoServicoAndPrioridade(String tipoServico, String prioridade);
    boolean existsByTipoServicoAndPrioridadeAndIdNot(String tipoServico, String prioridade, Integer id);
}
