package com.example.backend.sys.perfilPermissao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilPermissaoRepository extends JpaRepository<PerfilPermissao, Integer> {
    boolean existsByPerfilId(Integer perfilId);
    boolean existsByPermissaoId(Integer permissaoId);
    boolean existsByPerfilIdAndPermissaoId(Integer perfilId, Integer permissaoId);
    boolean existsByPerfilIdAndPermissaoIdAndIdNot(Integer perfilId, Integer permissaoId, Integer id);
    boolean existsByPerfilIdAndIdNot(Integer perfilId, Integer id);
}
