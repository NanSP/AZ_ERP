package com.example.backend.core.enderecos;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EnderecosRepository extends JpaRepository<Enderecos, Integer> {
    @Modifying
    @Transactional
    @Query("""
            update Enderecos e
               set e.principal = false
             where e.entidadeTipo = :entidadeTipo
               and e.entidadeId = :entidadeId
               and (:idAtual is null or e.id <> :idAtual)
            """)
    void clearPrincipalByEntidade(
            String entidadeTipo,
            Integer entidadeId,
            Integer idAtual
    );
}
