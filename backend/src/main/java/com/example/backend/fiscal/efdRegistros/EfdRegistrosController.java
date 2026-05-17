package com.example.backend.fiscal.efdRegistros;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/efdRegistros")
public class EfdRegistrosController {

    private final EfdRegistrosRepository repository;

    public EfdRegistrosController(EfdRegistrosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EfdRegistrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EfdRegistrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EfdRegistrosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEfdRegistros(@RequestBody EfdRegistrosRequestDTO data) {
        EfdRegistros entity = new EfdRegistros();
        entity.setPeriodo(data.periodo());
        entity.setRegistro(data.registro());
        entity.setConteudo(data.conteudo());
        entity.setCreatedAt(data.createdAt());

        EfdRegistros saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EfdRegistrosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEfdRegistros(@PathVariable Long id, @RequestBody EfdRegistrosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setPeriodo(data.periodo());
                    entity.setRegistro(data.registro());
                    entity.setConteudo(data.conteudo());
                    entity.setCreatedAt(data.createdAt());

                    EfdRegistros updated = repository.save(entity);
                    return ResponseEntity.ok(new EfdRegistrosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEfdRegistros(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("EFD registro deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}