package com.example.backend.grc.auditorias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;

public interface AuditoriasRepository extends JpaRepository<Auditorias, Integer> {
    @Query("""
            select count(a) > 0
            from Auditorias a
            where a.responsavel.id = :responsavelId
              and a.status in :statuses
              and a.dataInicio is not null
              and a.dataInicio <= :dataFimComparacao
              and coalesce(a.dataFim, a.dataInicio) >= :dataInicioComparacao
            """)
    boolean existsByResponsavelIdAndStatusInAndPeriodoSobreposto(
            Integer responsavelId,
            Collection<String> statuses,
            LocalDate dataFimComparacao,
            LocalDate dataInicioComparacao
    );

    @Query("""
            select count(a) > 0
            from Auditorias a
            where a.responsavel.id = :responsavelId
              and a.status in :statuses
              and a.id <> :id
              and a.dataInicio is not null
              and a.dataInicio <= :dataFimComparacao
              and coalesce(a.dataFim, a.dataInicio) >= :dataInicioComparacao
            """)
    boolean existsByResponsavelIdAndStatusInAndPeriodoSobrepostoAndIdNot(
            Integer responsavelId,
            Collection<String> statuses,
            LocalDate dataFimComparacao,
            LocalDate dataInicioComparacao,
            Integer id
    );
}
