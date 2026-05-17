package com.example.backend.sd.oportunidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OportunidadesResponseDTO
        (
                Integer id,
                Integer cliente,
                String titulo,
                String descricao,
                BigDecimal valorEstimado,
                Integer probabilidade,
                String estagio,
                LocalDate dataPrevistaFechamento,
                String motivoPerda,
                Integer responsavel,
                LocalDateTime createdAt
        )
    {
        public OportunidadesResponseDTO(Oportunidades oportunidades) {
            this
                    (
                            oportunidades.getId(),
                            oportunidades.getCliente() != null ? oportunidades.getCliente().getId() : null,
                            oportunidades.getTitulo(),
                            oportunidades.getDescricao(),
                            oportunidades.getValorEstimado(),
                            oportunidades.getProbabilidade(),
                            oportunidades.getEstagio(),
                            oportunidades.getDataPrevistaFechamento(),
                            oportunidades.getMotivoPerda(),
                            oportunidades.getResponsavel() != null ? oportunidades.getResponsavel().getId() : null,
                            oportunidades.getCreatedAt()
                    );
        }
}
