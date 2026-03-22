package sys.usuarioPerfil;

import sys.perfis.Perfis;
import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record UsuarioPerfilRequestDTO
        (
                Usuarios usuarioId,
                Perfis perfilId,
                LocalDateTime dataAtribuicao
        ) {
}
