package sys.usuarioPerfil;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import sys.perfis.Perfis;
import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record UsuarioPerfilResponseDTO
        (
                Integer id,
                Usuarios usuarioId,
                Perfis perfilId,
                LocalDateTime dataAtribuicao
        )
    {
        public UsuarioPerfilResponseDTO(UsuarioPerfil usuarioPerfil) {
            this(
                    usuarioPerfil.getId(),
                    usuarioPerfil.getUsuarioId(),
                    usuarioPerfil.getPerfilId(),
                    usuarioPerfil.getDataAtribuicao()
            );
        }
}
