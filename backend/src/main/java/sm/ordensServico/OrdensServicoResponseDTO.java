package sm.ordensServico;

import core.parceiros.Parceiros;
import core.produtos.Produtos;
import rh.colaboradores.Colaboradores;

import java.time.LocalDateTime;

public record OrdensServicoResponseDTO
        (
                Integer id,
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
    public OrdensServicoResponseDTO(OrdensServico ordensServico) {
        this
                (
                        ordensServico.getId(),
                        ordensServico.getNumeroOs(),
                        ordensServico.getClienteId(),
                        ordensServico.getProdutoId(),
                        ordensServico.getTipoServico(),
                        ordensServico.getDescricaoProblema(),
                        ordensServico.getPrioridade(),
                        ordensServico.getDataAbertura(),
                        ordensServico.getDataAgendamento(),
                        ordensServico.getDataInicio(),
                        ordensServico.getDataFim(),
                        ordensServico.getTecnicoId(),
                        ordensServico.getStatus(),
                        ordensServico.getCreatedAt()
                );
    }
}
