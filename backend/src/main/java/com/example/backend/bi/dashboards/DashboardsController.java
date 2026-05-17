package com.example.backend.bi.dashboards;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/dashboards")
public class DashboardsController {

    private final DashboardsRepository repository;

    public DashboardsController(DashboardsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DashboardsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DashboardsResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new DashboardsResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveDashboards(@RequestBody DashboardsRequestDTO data) {
        Dashboards entity = new Dashboards(data);
        Dashboards saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DashboardsResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDashboards(@PathVariable Integer id, @RequestBody DashboardsRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setLayout(data.layout());
                    entity.setConfiguracoes(data.configuracoes());
                    entity.setCreatedAt(data.createdAt());

                    Dashboards updated = repository.save(entity);
                    return ResponseEntity.ok(new DashboardsResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDashboards(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Dashboard deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}