package com.example.backend.sm.slaConfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/slaConfig")
public class SlaConfigController {

    private final SlaConfigRepository repository;

    public SlaConfigController(SlaConfigRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SlaConfigResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SlaConfigResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new SlaConfigResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveSlaConfig(@RequestBody SlaConfigRequestDTO data) {
        SlaConfig entity = new SlaConfig(data);
        SlaConfig saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SlaConfigResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlaConfig(@PathVariable Integer id, @RequestBody SlaConfigRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setTipoServico(data.tipoServico());
                    entity.setPrioridade(data.prioridade());
                    entity.setTempoAtendimentoHoras(data.tempoAtendimentoHoras());
                    entity.setTempoResolucaoHoras(data.tempoResolucaoHoras());
                    entity.setCreatedAt(data.createdAt());

                    SlaConfig updated = repository.save(entity);
                    return ResponseEntity.ok(new SlaConfigResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlaConfig(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("SLA config deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}