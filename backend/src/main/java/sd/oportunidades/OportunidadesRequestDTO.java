package sd.oportunidades;

import sd.clientes.Clientes;
import sys.usuarios.Usuarios;

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
