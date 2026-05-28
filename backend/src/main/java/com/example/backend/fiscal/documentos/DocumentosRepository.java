package com.example.backend.fiscal.documentos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentosRepository extends JpaRepository<Documentos, Integer> {
    boolean existsByChaveAcesso(String chaveAcesso);
    boolean existsByChaveAcessoAndIdNot(String chaveAcesso, Integer id);
}
