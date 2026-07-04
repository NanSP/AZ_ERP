package com.example.backend.grc.registrosTratamento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrosTratamentoRepository extends JpaRepository<RegistrosTratamento, Integer> {
    boolean existsByModuloAndEntidadeAndFinalidade(String modulo, String entidade, String finalidade);
    boolean existsByModuloAndEntidadeAndFinalidadeAndIdNot(String modulo, String entidade, String finalidade, Integer id);
}
