package com.example.backend.sd.oportunidades;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sd.clientes.Clientes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OportunidadesResponseDTO
        (
                Integer id,
                Clientes clienteId,
                String titulo,
                String descricao,
                BigDecimal valorEstimado,
                Integer probabilidade,
                String estagio,
                LocalDate dataPrevistaFechamento,
                String motivoPerda,
                Usuarios responsavelId,
                LocalDateTime createdAt
        )
    {
        public OportunidadesResponseDTO(Oportunidades oportunidades) {
            this
                    (
                            oportunidades.getId(),
                            oportunidades.getClienteId(),
                            oportunidades.getTitulo(),
                            oportunidades.getDescricao(),
                            oportunidades.getValorEstimado(),
                            oportunidades.getProbabilidade(),
                            oportunidades.getEstagio(),
                            oportunidades.getDataPrevistaFechamento(),
                            oportunidades.getMotivoPerda(),
                            oportunidades.getResponsavelId(),
                            oportunidades.getCreatedAt()
                    );
        }
}
