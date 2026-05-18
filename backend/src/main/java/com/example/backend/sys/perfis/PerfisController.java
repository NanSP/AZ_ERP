package com.example.backend.sys.perfis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/perfis")
public class PerfisController {

    private final PerfisRepository repository;

    public PerfisController(PerfisRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PerfisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PerfisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new PerfisResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> savePerfis(@RequestBody PerfisRequestDTO data) {
        Perfis entity = new Perfis(data);
        Perfis saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PerfisResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfis(@PathVariable Integer id, @RequestBody PerfisRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setNivelAcesso(data.nivelAcesso());
                    entity.setCreatedAt(data.createdAt());

                    Perfis updated = repository.save(entity);
                    return ResponseEntity.ok(new PerfisResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerfis(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Perfil deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}