package com.example.backend.grc.solicitacoesTitular;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacoesTitularRepository extends JpaRepository<SolicitacoesTitular, Integer> {
    boolean existsByProtocolo(String protocolo);
}
