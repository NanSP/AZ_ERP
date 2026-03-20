package core.enderecos;

import java.time.LocalDateTime;

public record EnderecosRequestDTO
        (
                String entidadeTipo,
                Integer entidadeId,
                String tipoEndereco,
                String logradouro,
                String numero,
                String complemento,
                String bairro,
                String cidade,
                String uf,
                String cep,
                String pais,
                Boolean principal,
                LocalDateTime createdAt
        ) {
}
