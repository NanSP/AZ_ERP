package core.enderecos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "enderecos", schema = "core")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Enderecos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entidade_tipo")
    private String entidadeTipo;
    @Column(name = "entidade_id")
    private Integer entidadeId;
    @Column(name = "tipo_endereco")
    private String tipoEndereco;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String pais;
    private Boolean principal;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Enderecos(EnderecosRequestDTO data){

        this.entidadeTipo = data.entidadeTipo();
        this.entidadeId = data.entidadeId();
        this.tipoEndereco = data.tipoEndereco();
        this.logradouro = data.logradouro();
        this.numero = data.numero();
        this.complemento = data.complemento();
        this.bairro = data.bairro();
        this.cidade = data.cidade();
        this.uf = data.uf();
        this.cep = data.cep();
        this.pais = data.pais();
        this.principal = data.principal();
        this.createdAt = data.createdAt();
    }
}
