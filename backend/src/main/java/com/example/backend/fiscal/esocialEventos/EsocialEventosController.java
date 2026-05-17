package com.example.backend.fiscal.esocialEventos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/esocialEventos")
public class EsocialEventosController {

    private final EsocialEventosRepository repository;

    public EsocialEventosController(EsocialEventosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EsocialEventosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EsocialEventosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EsocialEventosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEsocialEventos(@RequestBody EsocialEventosRequestDTO data) {
        EsocialEventos entity = new EsocialEventos();
        entity.setPeriodoApuracao(data.periodoApuracao());
        entity.setTipoEvento(data.tipoEvento());
        entity.setEventoId(data.eventoId());
        entity.setConteudo(data.conteudo());
        entity.setStatus(data.status());
        entity.setCreatedAt(data.createdAt());

        EsocialEventos saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EsocialEventosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEsocialEventos(@PathVariable Long id, @RequestBody EsocialEventosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setPeriodoApuracao(data.periodoApuracao());
                    entity.setTipoEvento(data.tipoEvento());
                    entity.setEventoId(data.eventoId());
                    entity.setConteudo(data.conteudo());
                    entity.setStatus(data.status());
                    entity.setCreatedAt(data.createdAt());

                    EsocialEventos updated = repository.save(entity);
                    return ResponseEntity.ok(new EsocialEventosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEsocialEventos(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("eSocial evento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}