package mm.inventarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InventariosResponseDTO
        (
                Integer id,
                LocalDate dataInicio,
                LocalDate dataFim,
                String tipoInventario,
                String status,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public InventariosResponseDTO(Inventarios inventarios) {
            this
                    (
                            inventarios.getId(),
                            inventarios.getDataInicio(),
                            inventarios.getDataFim(),
                            inventarios.getTipoInventario(),
                            inventarios.getStatus(),
                            inventarios.getObservacoes(),
                            inventarios.getCreatedAt()
                    );
        }
}
