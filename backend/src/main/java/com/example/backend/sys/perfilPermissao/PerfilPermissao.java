package com.example.backend.sys.perfilPermissao;

import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.permissoes.Permissoes;
import jakarta.persistence.*;
import lombok.*;

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
    private Perfis perfil;

    @ManyToOne
    @JoinColumn(name = "permissao_id")
    private Permissoes permissao;

}
