package sys.perfilPermissao;

import jakarta.persistence.*;
import lombok.*;
import sys.perfis.Perfis;
import sys.permissoes.Permissoes;

@Table(name = "perfil_permissao", schema = "sys")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PerfilPermissao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "perfil_id")
    private Perfis perfilId;

    @ManyToOne
    @JoinColumn(name = "permissao_id")
    private Permissoes permissaoId;

    public PerfilPermissao(PerfilPermissaoRequestDTO data){
        this.permissaoId = data.permissaoId();
        this.perfilId = data.perfilId();
    }
}
