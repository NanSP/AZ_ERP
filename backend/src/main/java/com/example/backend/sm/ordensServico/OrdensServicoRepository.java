package com.example.backend.sm.ordensServico;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdensServicoRepository extends JpaRepository<OrdensServico, Integer> {
    boolean existsByNumeroOs(String numeroOs);
    boolean existsByNumeroOsAndIdNot(String numeroOs, Integer id);
    boolean existsByProdutoId(Integer produtoId);
}
