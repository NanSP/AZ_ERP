package com.example.backend.bi.metricas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/metricas")
public class MetricasController {

    private final MetricasRepository repository;

    public MetricasController(MetricasRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MetricasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MetricasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new MetricasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveMetricas(@RequestBody MetricasRequestDTO data) {
        Metricas entity = new Metricas(data);
        Metricas saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MetricasResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMetricas(@PathVariable Integer id, @RequestBody MetricasRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setCategoria(data.categoria());
                    entity.setFormula(data.formula());
                    entity.setUnidadeMedida(data.unidadeMedida());
                    entity.setMeta(data.meta());
                    entity.setCreatedAt(data.createdAt());

                    Metricas updated = repository.save(entity);
                    return ResponseEntity.ok(new MetricasResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMetricas(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Metrica deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}