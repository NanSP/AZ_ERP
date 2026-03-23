package sys.usuarioPerfil;

import jakarta.persistence.*;
import lombok.*;
import rh.colaboradores.Colaboradores;
import sys.perfis.Perfis;
import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

@Table(name = "usuario_perfil", schema = "sys")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UsuarioPerfil {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarioId;

    @ManyToOne
    @JoinColumn(name = "perfil_id")
    private Perfis perfilId;

    @Column(name = "data_atribuicao")
    private LocalDateTime dataAtribuicao;

    public UsuarioPerfil(UsuarioPerfilRequestDTO data){

        this.usuarioId = data.usuarioId();
        this.perfilId = data.perfilId();
        this.dataAtribuicao = data.dataAtribuicao();
    }
}
