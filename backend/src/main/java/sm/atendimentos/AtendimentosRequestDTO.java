package sm.atendimentos;

import rh.colaboradores.Colaboradores;
import sm.ordensServico.OrdensServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentosRequestDTO
        (
                OrdensServico osId,
                Colaboradores tecnicoId,
                LocalDateTime dataHora,
                String descricao,
                BigDecimal horasGastas,
                Map<String, Object> materiaisUtilizados,
                LocalDateTime createdAt
        ) {
}
