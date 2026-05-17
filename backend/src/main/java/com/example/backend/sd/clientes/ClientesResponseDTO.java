package com.example.backend.sd.clientes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientesResponseDTO
        (
                Integer id,
                Integer parceiro,
                String classificacao,
                String origem,
                String website,
                BigDecimal faturamentoAnual,
                Integer numeroFuncionarios,
                LocalDateTime createdAt
        )
    {
        public ClientesResponseDTO(Clientes clientes) {
            this
                    (
                            clientes.getId(),
                            clientes.getParceiro() != null ? clientes.getParceiro().getId() : null,
                            clientes.getClassificacao(),
                            clientes.getOrigem(),
                            clientes.getWebsite(),
                            clientes.getFaturamentoAnual(),
                            clientes.getNumeroFuncionarios(),
                            clientes.getCreatedAt()
                    );
        }
}
