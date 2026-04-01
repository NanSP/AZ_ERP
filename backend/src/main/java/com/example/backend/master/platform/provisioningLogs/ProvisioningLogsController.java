package com.example.backend.master.platform.provisioningLogs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/provisioningLogs")
public class ProvisioningLogsController {

    private final ProvisioningLogsRepository repository;
    private final ProvisioningLogsService service;

    public ProvisioningLogsController(
            ProvisioningLogsRepository repository,
            ProvisioningLogsService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ProvisioningLogsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProvisioningLogsResponseDTO::new)
                .toList();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(log -> ResponseEntity.ok(new ProvisioningLogsResponseDTO(log)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<?> saveProvisioningLog(@RequestBody ProvisioningLogsRequestDTO data) {
        ProvisioningLogs saved = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProvisioningLogsResponseDTO(saved));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvisioningLogs(@PathVariable Long id, @RequestBody ProvisioningLogsRequestDTO data) {
        try {
            ProvisioningLogs updated = service.update(id, data);
            return ResponseEntity.ok(new ProvisioningLogsResponseDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvisioningLogs(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(log -> {
                    repository.delete(log);
                    return ResponseEntity.ok("ProvisioningLogs deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }
}
