package com.example.backend.core.contatos;

import java.time.LocalDateTime;

public record ContatosResponseDTO
        (
                Integer id,
                String entidadeTipo,
                Integer entidadeId,
                String tipoContato,
                String valor,
                Boolean principal,
                String observacao,
                LocalDateTime createdAt
        )
    {

    public ContatosResponseDTO(Contatos contatos){
        this(
            contatos.getId(),
            contatos.getEntidadeTipo(),
            contatos.getEntidadeId(),
            contatos.getTipoContato(),
            contatos.getValor(),
            contatos.getPrincipal(),
            contatos.getObservacao(),
            contatos.getCreatedAt()
        );
    }
}
