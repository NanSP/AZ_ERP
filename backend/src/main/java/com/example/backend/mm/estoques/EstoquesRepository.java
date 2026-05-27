package com.example.backend.mm.estoques;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoquesRepository extends JpaRepository<Estoques, Integer> {
    boolean existsByProdutoId(Integer produtoId);

    boolean existsByProdutoIdAndEmpresaIdAndLocalizacaoAndLote(
            Integer produtoId,
            Integer empresaId,
            String localizacao,
            String lote
    );

    boolean existsByProdutoIdAndEmpresaIdAndLocalizacaoAndLoteAndIdNot(
            Integer produtoId,
            Integer empresaId,
            String localizacao,
            String lote,
            Integer id
    );
}
