package com.example.backend.portal.notificacoes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacoesRepository extends JpaRepository<Notificacoes, Integer> {
    boolean existsByUsuarioIdAndTituloAndMensagemAndTipoAndLidaFalse(
            Integer usuarioId,
            String titulo,
            String mensagem,
            String tipo
    );

    boolean existsByUsuarioIdAndTituloAndMensagemAndTipoAndLidaFalseAndIdNot(
            Integer usuarioId,
            String titulo,
            String mensagem,
            String tipo,
            Integer id
    );
}
