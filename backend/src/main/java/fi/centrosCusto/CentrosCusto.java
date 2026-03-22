package fi.centrosCusto;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "centros_custo", schema = "financeiro")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CentrosCusto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String codigo;
    private String nome;
    private String tipo;
    private String responsavel;
    private Boolean ativo;

    public CentrosCusto(CentrosCustoRequestDTO data){
        this.codigo = data.codigo();
        this.nome = data.nome();
        this.tipo = data.tipo();
        this.ativo = data.ativo();
    }
}
