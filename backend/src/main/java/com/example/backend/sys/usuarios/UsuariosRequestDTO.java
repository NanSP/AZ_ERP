package com.example.backend.sys.usuarios;

import com.example.backend.auditoria.logAcoes.LogAcoes;
import com.example.backend.auditoria.logErros.LogErros;
import com.example.backend.portal.dispositivos.Dispositivos;
import com.example.backend.portal.notificacoes.Notificacoes;
import com.example.backend.portal.sessoes.Sessoes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UsuariosRequestDTO
        (
                String nome,
                String email,
                String login,
                String senhaHash,
                String documento,
                String tipoUsuario,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDate expiracaoSenha,
                Integer tentativasLogin,
                List<Sessoes> sessoes,
                List<LogAcoes> logAcoes,
                List<LogErros> logErros,
                List<Notificacoes> notificacoes,
                List<Dispositivos> dispositivos,
                LocalDateTime createdAt
        ) {
}
