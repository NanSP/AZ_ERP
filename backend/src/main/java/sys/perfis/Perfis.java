package sys.perfis;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "perfis", schema = "sys")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Perfis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;
    @Column(name = "nivel_acesso")
    private Integer nivelAcesso;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Perfis(PerfisRequestDTO data){
        this.nome = data.nome();
        this.descricao = data.descricao();
        this.nivelAcesso = data.nivelAcesso();
        this.createdAt = data.createdAt();
    }
}
