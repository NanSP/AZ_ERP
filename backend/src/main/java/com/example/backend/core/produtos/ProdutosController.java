package com.example.backend.core.produtos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/produtos")
public class ProdutosController {

    private final ProdutosRepository repository;

    public ProdutosController(ProdutosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProdutosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProdutosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ProdutosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveProduto(@RequestBody ProdutosRequestDTO data) {
        Produtos entity = new Produtos(data);
        Produtos saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduto(@PathVariable Integer id, @RequestBody ProdutosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setCodigo(data.codigo());
                    entity.setCodigoBarras(data.codigoBarras());
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setTipoItem(data.tipoItem());
                    entity.setUnidadeMedida(data.unidadeMedida());
                    entity.setNcm(data.ncm());
                    entity.setCest(data.cest());
                    entity.setPesoBruto(data.pesoBruto());
                    entity.setPesoLiquido(data.pesoLiquido());
                    entity.setOrigem(data.origem());
                    entity.setSituacao(data.situacao());
                    entity.setCreatedAt(data.createdAt());

                    Produtos updated = repository.save(entity);
                    return ResponseEntity.ok(new ProdutosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduto(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Produto deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}