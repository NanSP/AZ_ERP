package sys.permissoes;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "permissoes", schema = "sys")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Permissoes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;
    private String modulo;
    private String recurso;
    private String acao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Permissoes(PermissoesRequestDTO data){

        this.nome = data.nome();
        this.descricao = data.descricao();
        this.modulo = data.modulo();
        this.recurso = data.recurso();
        this.acao = data.acao();
        this.createdAt = data.createdAt();
    }
}
