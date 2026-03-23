package com.example.backend.sd.oportunidades;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sd.clientes.Clientes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OportunidadesRequestDTO
        (
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
        ) {
}
