package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;

import java.time.LocalDateTime;

public record MateriaisRequestDTO(Produtos produtoId,
                                  String tipoMaterial,
                                  String categoria,
                                  String subcategoria,
                                  String marca,
                                  String modelo,
                                  String especificacoesTecnicas,
                                  String condicaoArmazenamento,
                                  String classePerigo,
                                  LocalDateTime createdAt) {
}
