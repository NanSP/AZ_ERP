package com.example.backend.grc.registrosTratamento;

import com.example.backend.sys.usuarios.Usuarios;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "registros_tratamento", schema = "grc")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class RegistrosTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String modulo;
    private String entidade;
    private String finalidade;

    @Column(name = "base_legal")
    private String baseLegal;

    @Column(name = "categoria_titular")
    private String categoriaTitular;

    @Column(name = "categoria_dados")
    private String categoriaDados;

    @Column(name = "retencao_dias")
    private Integer retencaoDias;

    private String compartilhamento;

    @Column(name = "requer_consentimento")
    private Boolean requerConsentimento;

    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
