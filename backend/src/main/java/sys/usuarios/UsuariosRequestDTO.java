package sys.usuarios;

import auditoria.logAcoes.LogAcoes;
import auditoria.logErros.LogErros;
import portal.dispositivos.Dispositivos;
import portal.notificacoes.Notificacoes;
import portal.sessoes.Sessoes;

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
