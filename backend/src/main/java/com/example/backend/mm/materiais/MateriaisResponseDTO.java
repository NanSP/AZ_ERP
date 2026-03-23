package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;

import java.time.LocalDateTime;

public record MateriaisResponseDTO
        (
                Integer id,
                Produtos produtoId,
                String tipoMaterial,
                String categoria,
                String subcategoria,
                String marca,
                String modelo,
                String especificacoesTecnicas,
                String condicaoArmazenamento,
                String classePerigo,
                LocalDateTime createdAt
        )
    {
        public MateriaisResponseDTO(Materiais materiais) {
            this
                    (
                            materiais.getId(),
                            materiais.getProdutoId(),
                            materiais.getTipoMaterial(),
                            materiais.getCategoria(),
                            materiais.getSubcategoria(),
                            materiais.getMarca(),
                            materiais.getModelo(),
                            materiais.getEspecificacoesTecnicas(),
                            materiais.getCondicaoArmazenamento(),
                            materiais.getClassePerigo(),
                            materiais.getCreatedAt()
                    );
        }
}
