package com.example.backend.fi.centrosCusto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/centrosCusto")
public class CentrosCustoController {

    private final CentrosCustoRepository repository;

    public CentrosCustoController(CentrosCustoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CentrosCustoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(CentrosCustoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new CentrosCustoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveCentrosCusto(@RequestBody CentrosCustoRequestDTO data) {
        CentrosCusto entity = new CentrosCusto(data);
        CentrosCusto saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CentrosCustoResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCentrosCusto(@PathVariable Integer id, @RequestBody CentrosCustoRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setCodigo(data.codigo());
                    entity.setNome(data.nome());
                    entity.setTipo(data.tipo());
                    entity.setResponsavel(data.responsavel());
                    entity.setAtivo(data.ativo());

                    CentrosCusto updated = repository.save(entity);
                    return ResponseEntity.ok(new CentrosCustoResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCentrosCusto(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Centro de custo deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}