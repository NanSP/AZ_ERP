package fiscal.documentos;

import core.parceiros.Parceiros;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DocumentosResponseDTO
        (
                Integer id,
                String tipoDocumento,
                String numero,
                String serie,
                String chaveAcesso,
                LocalDateTime dataEmissao,
                Pedidos pedidoId,
                Parceiros clienteId,
                BigDecimal valorTotal,
                String status,
                String xml_file,
                LocalDateTime createdAt
        ) {
    public DocumentosResponseDTO(Documentos documentos) {
        this
                (
                        documentos.getId(),
                        documentos.getTipoDocumento(),
                        documentos.getNumero(),
                        documentos.getSerie(),
                        documentos.getChaveAcesso(),
                        documentos.getDataEmissao(),
                        documentos.getPedidoId(),
                        documentos.getClienteId(),
                        documentos.getValorTotal(),
                        documentos.getStatus(),
                        documentos.getXml_file(),
                        documentos.getCreatedAt()
                );
    }
}
