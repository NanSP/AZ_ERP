package sm.ordensServico;

import core.parceiros.Parceiros;
import core.produtos.Produtos;
import rh.colaboradores.Colaboradores;

import java.time.LocalDateTime;

public record OrdensServicoRequestDTO
        (
                String numeroOs,
                Parceiros clienteId,
                Produtos produtoId,
                String tipoServico,
                String descricaoProblema,
                String prioridade,
                LocalDateTime dataAbertura,
                LocalDateTime dataAgendamento,
                LocalDateTime dataInicio,
                LocalDateTime dataFim,
                Colaboradores tecnicoId,
                String status,
                LocalDateTime createdAt
        ) {
}
