package com.example.backend.master.platform.tenantDatabases;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/tenantDatabases")
public class TenantDatabasesController {

    private final TenantDatabasesRepository repository;
    private final TenantDatabasesService service;

    public TenantDatabasesController(
            TenantDatabasesRepository repository,
            TenantDatabasesService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TenantDatabasesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TenantDatabasesResponseDTO::new)
                .toList();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new TenantDatabasesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<?> saveTenantDatabases(@RequestBody TenantDatabasesRequestDTO data) {
        TenantDatabases saved = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TenantDatabasesResponseDTO(saved));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenantDatabases(@PathVariable Long id, @RequestBody TenantDatabasesRequestDTO data) {
        try {
            TenantDatabases updated = service.update(id, data);
            return ResponseEntity.ok(new TenantDatabasesResponseDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenantDatabases(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("TenantDatabases deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }
}
