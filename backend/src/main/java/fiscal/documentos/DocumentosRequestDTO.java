package fiscal.documentos;

import core.parceiros.Parceiros;
import sd.pedidos.Pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DocumentosRequestDTO
        (
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
}
