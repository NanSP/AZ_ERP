package rh.colaboradores;

import jakarta.persistence.Column;
import rh.beneficios.Beneficios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ColaboradoresResponseDTO(
         Integer id,
         String codigo,
         String nome,
         String cpf,
         String rg,
         LocalDate dataNascimento,
         String sexo,
         String estadoCivil,
         String nacionalidade,
         String emailPessoal,
         String emailCorporativo,
         String telefone,
         String celular,
         LocalDate dataAdmissao,
         LocalDate dataDemissao,
         String cargo,
         String departamento,
         BigDecimal salario,
         String tipoContrato,
         Integer jornadaSemanal,
         String situacao,
         LocalDateTime createdAt
) {
    public ColaboradoresResponseDTO(Colaboradores colaboradores){
        this(
                colaboradores.getId(),
                colaboradores.getCodigo(),
                colaboradores.getNome(),
                colaboradores.getCpf(),
                colaboradores.getRg(),
                colaboradores.getDataNascimento(),
                colaboradores.getSexo(),
                colaboradores.getEstadoCivil(),
                colaboradores.getNacionalidade(),
                colaboradores.getEmailPessoal(),
                colaboradores.getEmailCorporativo(),
                colaboradores.getTelefone(),
                colaboradores.getCelular(),
                colaboradores.getDataAdmissao(),
                colaboradores.getDataDemissao(),
                colaboradores.getCargo(),
                colaboradores.getDepartamento(),
                colaboradores.getSalario(),
                colaboradores.getTipoContrato(),
                colaboradores.getJornadaSemanal(),
                colaboradores.getSituacao(),
                colaboradores.getCreatedAt()
        );
    }
}
