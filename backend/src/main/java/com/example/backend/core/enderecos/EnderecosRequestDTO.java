package com.example.backend.core.enderecos;


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
                Boolean principal
        ) {
}
