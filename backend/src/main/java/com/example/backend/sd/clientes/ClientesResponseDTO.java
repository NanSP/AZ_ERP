package com.example.backend.sd.clientes;

import com.example.backend.core.parceiros.Parceiros;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientesResponseDTO
        (
                Integer id,
                Parceiros parceiroId,
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
                            clientes.getParceiroId(),
                            clientes.getClassificacao(),
                            clientes.getOrigem(),
                            clientes.getWebsite(),
                            clientes.getFaturamentoAnual(),
                            clientes.getNumeroFuncionarios(),
                            clientes.getCreatedAt()
                    );
        }
}
