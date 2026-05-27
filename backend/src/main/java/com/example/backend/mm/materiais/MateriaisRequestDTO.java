package com.example.backend.mm.materiais;


public record MateriaisRequestDTO(

        Integer produto,
        String tipoMaterial,
        String categoria,
        String subcategoria,
        String marca,
        String modelo,
        String especificacoesTecnicas,
        String condicaoArmazenamento,
        String classePerigo
    ){
}
