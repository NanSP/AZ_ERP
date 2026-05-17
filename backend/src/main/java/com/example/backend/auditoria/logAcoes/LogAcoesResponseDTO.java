package com.example.backend.auditoria.logAcoes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogAcoesResponseDTO
        (
                Long id,
                Integer usuario,
                String modulo,
                String acao,
                String tabela,
                Integer registroId,
                Map<String, Object> dadosAntigos,
                Map<String, Object> dadosNovos,
                InetAddress ipAddress,
                String userAgent,
                LocalDateTime createdAt
        ) {
    public LogAcoesResponseDTO(LogAcoes logAcoes) {
        this
                (
                        logAcoes.getId(),
                        logAcoes.getUsuario() != null ? logAcoes.getUsuario().getId() : null,
                        logAcoes.getModulo(),
                        logAcoes.getAcao(),
                        logAcoes.getTabela(),
                        logAcoes.getRegistroId(),
                        logAcoes.getDadosAntigos(),
                        logAcoes.getDadosNovos(),
                        logAcoes.getIpAddress(),
                        logAcoes.getUserAgent(),
                        logAcoes.getCreatedAt()
                );
    }

}
