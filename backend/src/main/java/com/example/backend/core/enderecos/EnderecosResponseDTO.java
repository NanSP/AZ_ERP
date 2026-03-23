package com.example.backend.core.enderecos;

import java.time.LocalDateTime;

public record EnderecosResponseDTO
        (
                Integer id,
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
        )
    {
        public EnderecosResponseDTO(Enderecos enderecos) {
            this
                    (
                            enderecos.getId(),
                            enderecos.getEntidadeTipo(),
                            enderecos.getEntidadeId(),
                            enderecos.getTipoEndereco(),
                            enderecos.getLogradouro(),
                            enderecos.getNumero(),
                            enderecos.getComplemento(),
                            enderecos.getBairro(),
                            enderecos.getCidade(),
                            enderecos.getUf(),
                            enderecos.getCep(),
                            enderecos.getPais(),
                            enderecos.getPrincipal(),
                            enderecos.getCreatedAt()

                    );
        }
}
