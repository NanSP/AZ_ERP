package am.bensPatrimoniais;

import core.parceiros.Parceiros;
import rh.colaboradores.Colaboradores;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BensPatrimoniaisResponseDTO
        (
                Integer id,
                String codigoPatrimonio,
                String nome,
                String descricao,
                String tipoAtivo,
                String localizacao,
                LocalDate dataAquisicao,
                BigDecimal valorAquisicao,
                BigDecimal valorAtual,
                Integer vidaUtilAnos,
                BigDecimal taxaDepreciacao,
                LocalDate dataDepreciacao,
                Parceiros fornecedorId,
                Colaboradores responsavelId,
                String status,
                LocalDateTime createdAt
        ) {
    public BensPatrimoniaisResponseDTO(BensPatrimoniais bensPatrimoniais) {
        this
                (
                        bensPatrimoniais.getId(),
                        bensPatrimoniais.getCodigoPatrimonio(),
                        bensPatrimoniais.getNome(),
                        bensPatrimoniais.getDescricao(),
                        bensPatrimoniais.getTipoAtivo(),
                        bensPatrimoniais.getLocalizacao(),
                        bensPatrimoniais.getDataAquisicao(),
                        bensPatrimoniais.getValorAquisicao(),
                        bensPatrimoniais.getValorAtual(),
                        bensPatrimoniais.getVidaUtilAnos(),
                        bensPatrimoniais.getTaxaDepreciacao(),
                        bensPatrimoniais.getDataDepreciacao(),
                        bensPatrimoniais.getFornecedorId(),
                        bensPatrimoniais.getResponsavelId(),
                        bensPatrimoniais.getStatus(),
                        bensPatrimoniais.getCreatedAt()
                );
    }
}
