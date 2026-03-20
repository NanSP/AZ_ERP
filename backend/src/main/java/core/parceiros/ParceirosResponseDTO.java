package core.parceiros;

import core.enderecos.Enderecos;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParceirosResponseDTO
        (
                Integer id,
                String tipoParceiro,
                String codigo,
                String nome,
                String nomeFantasia,
                String documento,
                String tipoPessoa,
                String situacao,
                BigDecimal limiteCredito,
                Integer diasPrazo,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public ParceirosResponseDTO(Parceiros parceiros) {
            this
                    (
                            parceiros.getId(),
                            parceiros.getTipoParceiro(),
                            parceiros.getCodigo(),
                            parceiros.getNome(),
                            parceiros.getNomeFantasia(),
                            parceiros.getDocumento(),
                            parceiros.getTipoPessoa(),
                            parceiros.getSituacao(),
                            parceiros.getLimiteCredito(),
                            parceiros.getDiasPrazo(),
                            parceiros.getObservacoes(),
                            parceiros.getCreatedAt()

                    );
        }
}
