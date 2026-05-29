package com.example.backend.core.contatos;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContatosRepository extends JpaRepository<Contatos, Integer> {
    @Modifying
    @Transactional
    @Query("""
            update Contatos c
               set c.principal = false
             where c.entidadeTipo = :entidadeTipo
               and c.entidadeId = :entidadeId
               and c.tipoContato = :tipoContato
               and (:idAtual is null or c.id <> :idAtual)
            """)
    void clearPrincipalByEntidadeAndTipoContato(
            String entidadeTipo,
            Integer entidadeId,
            String tipoContato,
            Integer idAtual
    );

    Optional<Contatos> findFirstByEntidadeTipoAndEntidadeIdAndTipoContatoAndIdNotOrderByIdAsc(
            String entidadeTipo,
            Integer entidadeId,
            String tipoContato,
            Integer id
    );
}
