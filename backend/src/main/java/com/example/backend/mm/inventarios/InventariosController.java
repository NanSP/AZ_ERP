package com.example.backend.mm.inventarios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/inventarios")
public class InventariosController {

    private final InventariosRepository repository;

    public InventariosController(InventariosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<InventariosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(InventariosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new InventariosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveInventarios(@RequestBody InventariosRequestDTO data) {
        Inventarios entity = new Inventarios();
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setTipoInventario(data.tipoInventario());
        entity.setStatus(data.status());
        entity.setObservacoes(data.observacoes());
        entity.setCreatedAt(data.createdAt());

        Inventarios saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InventariosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventarios(@PathVariable Integer id, @RequestBody InventariosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setDataInicio(data.dataInicio());
                    entity.setDataFim(data.dataFim());
                    entity.setTipoInventario(data.tipoInventario());
                    entity.setStatus(data.status());
                    entity.setObservacoes(data.observacoes());
                    entity.setCreatedAt(data.createdAt());

                    Inventarios updated = repository.save(entity);
                    return ResponseEntity.ok(new InventariosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventarios(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Inventario deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}