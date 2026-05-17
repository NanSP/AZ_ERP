package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/materiais")
public class MateriaisController {

    private final MateriaisRepository repository;
    private final ProdutosRepository produtosRepository;

    public MateriaisController(
            MateriaisRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<MateriaisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MateriaisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new MateriaisResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveMateriais(@RequestBody MateriaisRequestDTO data) {
        try {
            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Materiais entity = new Materiais();
            entity.setProduto(produto);
            entity.setTipoMaterial(data.tipoMaterial());
            entity.setCategoria(data.categoria());
            entity.setSubcategoria(data.subcategoria());
            entity.setMarca(data.marca());
            entity.setModelo(data.modelo());
            entity.setEspecificacoesTecnicas(data.especificacoesTecnicas());
            entity.setCondicaoArmazenamento(data.condicaoArmazenamento());
            entity.setClassePerigo(data.classePerigo());
            entity.setCreatedAt(data.createdAt());

            Materiais saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MateriaisResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMateriais(@PathVariable Integer id, @RequestBody MateriaisRequestDTO data) {
        try {
            Materiais entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Material nao encontrado"));

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            entity.setProduto(produto);
            entity.setTipoMaterial(data.tipoMaterial());
            entity.setCategoria(data.categoria());
            entity.setSubcategoria(data.subcategoria());
            entity.setMarca(data.marca());
            entity.setModelo(data.modelo());
            entity.setEspecificacoesTecnicas(data.especificacoesTecnicas());
            entity.setCondicaoArmazenamento(data.condicaoArmazenamento());
            entity.setClassePerigo(data.classePerigo());
            entity.setCreatedAt(data.createdAt());

            Materiais updated = repository.save(entity);
            return ResponseEntity.ok(new MateriaisResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMateriais(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Material deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}