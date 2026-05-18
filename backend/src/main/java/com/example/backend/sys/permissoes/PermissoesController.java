package com.example.backend.sys.permissoes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/permissoes")
public class PermissoesController {

    private final PermissoesRepository repository;

    public PermissoesController(PermissoesRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PermissoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PermissoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new PermissoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> savePermissoes(@RequestBody PermissoesRequestDTO data) {
        Permissoes entity = new Permissoes(data);
        Permissoes saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PermissoesResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermissoes(@PathVariable Integer id, @RequestBody PermissoesRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setModulo(data.modulo());
                    entity.setRecurso(data.recurso());
                    entity.setAcao(data.acao());
                    entity.setCreatedAt(data.createdAt());

                    Permissoes updated = repository.save(entity);
                    return ResponseEntity.ok(new PermissoesResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermissoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Permissao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}