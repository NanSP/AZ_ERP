package com.example.backend.pp.bom;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Integer> {
    boolean existsByProdutoPaiIdAndComponenteId(Integer produtoPaiId, Integer componenteId);
    boolean existsByProdutoPaiIdAndComponenteIdAndIdNot(Integer produtoPaiId, Integer componenteId, Integer id);
    List<Bom> findByProdutoPaiId(Integer produtoPaiId);
}
