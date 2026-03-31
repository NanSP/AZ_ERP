package com.example.backend.master.platform.tenants;

import java.time.LocalDateTime;

public record TenantsRequestDTO
        (
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
        ) {
}
