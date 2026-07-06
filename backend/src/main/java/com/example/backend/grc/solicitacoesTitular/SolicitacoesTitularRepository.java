package com.example.backend.grc.solicitacoesTitular;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitacoesTitularRepository extends JpaRepository<SolicitacoesTitular, Integer> {
    boolean existsByProtocolo(String protocolo);

    long countByStatusIgnoreCase(String status);

    long countByStatusIgnoreCaseIn(List<String> statuses);

    long countByPrazoRespostaBeforeAndStatusIgnoreCaseIn(LocalDateTime prazoResposta, List<String> statuses);

    long countByPrazoRespostaBetweenAndStatusIgnoreCaseIn(
            LocalDateTime start,
            LocalDateTime end,
            List<String> statuses
    );
}
