package com.example.backend.core.contatos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/contatos")
public class ContatosController {

    private final ContatosRepository repository;

    public ContatosController(ContatosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ContatosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContatosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ContatosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveContato(@RequestBody ContatosRequestDTO data) {
        Contatos entity = new Contatos(data);
        Contatos saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ContatosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContato(@PathVariable Integer id, @RequestBody ContatosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setEntidadeTipo(data.entidadeTipo());
                    entity.setEntidadeId(data.entidadeId());
                    entity.setTipoContato(data.tipoContato());
                    entity.setValor(data.valor());
                    entity.setPrincipal(data.principal());
                    entity.setObservacao(data.observacao());
                    entity.setCreatedAt(data.createdAt());

                    Contatos updated = repository.save(entity);
                    return ResponseEntity.ok(new ContatosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContato(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Contato deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}