package com.example.backend.master.platform.tenants;

import java.time.LocalDateTime;

public record TenantsResponseDTO
        (
                Integer id,
                String codigo,
                String nome,
                String nomeFantasia,
                String documento,
                String tipoDocumento,
                String emailResponsavel,
                String telefoneResponsavel,
                String status,
                String plano,
                String schemaVersion,
                String observacoes,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        )
    {
        public TenantsResponseDTO(Tenants tenants) {
            this
                    (
                            tenants.getId(),
                            tenants.getCodigo(),
                            tenants.getNome(),
                            tenants.getNomeFantasia(),
                            tenants.getDocumento(),
                            tenants.getTipoDocumento(),
                            tenants.getEmailResponsavel(),
                            tenants.getTelefoneResponsavel(),
                            tenants.getStatus(),
                            tenants.getPlano(),
                            tenants.getSchemaVersion(),
                            tenants.getObservacoes(),
                            tenants.getCreatedAt(),
                            tenants.getUpdatedAt()
                    );
        }
}
