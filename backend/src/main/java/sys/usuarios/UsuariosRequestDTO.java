package sys.usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
                LocalDateTime createdAt
        ) {
}
