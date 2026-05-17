package com.example.backend.fiscal.edcRegistros;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/ecdRegistros")
public class EcdRegistrosController {

    private final EcdRegistrosRepository repository;

    public EcdRegistrosController(EcdRegistrosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EcdRegistrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EcdRegistrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EcdRegistrosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEdcRegistros(@RequestBody EcdRegistrosRequestDTO data) {
        EcdRegistros entity = new EcdRegistros();
        entity.setPeriodo(data.periodo());
        entity.setRegistro(data.registro());
        entity.setConteudo(data.conteudo());
        entity.setCreatedAt(data.createdAt());

        EcdRegistros saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EcdRegistrosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEdcRegistros(@PathVariable Long id, @RequestBody EcdRegistrosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setPeriodo(data.periodo());
                    entity.setRegistro(data.registro());
                    entity.setConteudo(data.conteudo());
                    entity.setCreatedAt(data.createdAt());

                    EcdRegistros updated = repository.save(entity);
                    return ResponseEntity.ok(new EcdRegistrosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEcdRegistros(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("ECD registro deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}