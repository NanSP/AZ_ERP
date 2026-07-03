package com.example.backend.portal.sessoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SessoesRepository extends JpaRepository<Sessoes, Integer> {
    boolean existsByTokenSessao(String tokenSessao);
    boolean existsByTokenSessaoAndIdNot(String tokenSessao, Integer id);
    boolean existsByUsuarioIdAndDataLogoutIsNull(Integer usuarioId);
    boolean existsByUsuarioIdAndDataLogoutIsNullAndIdNot(Integer usuarioId, Integer id);
    long deleteByDataLogoutBefore(LocalDateTime dataLogout);
    long deleteByExpiracaoBefore(LocalDateTime expiracao);
}
