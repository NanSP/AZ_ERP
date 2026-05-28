package com.example.backend.portal.sessoes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessoesRepository extends JpaRepository<Sessoes, Integer> {
    boolean existsByTokenSessao(String tokenSessao);
    boolean existsByTokenSessaoAndIdNot(String tokenSessao, Integer id);
    boolean existsByUsuarioIdAndDataLogoutIsNull(Integer usuarioId);
    boolean existsByUsuarioIdAndDataLogoutIsNullAndIdNot(Integer usuarioId, Integer id);
}
