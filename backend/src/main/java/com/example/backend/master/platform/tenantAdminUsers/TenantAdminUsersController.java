package com.example.backend.master.platform.tenantAdminUsers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/tenantAdminUsers")
public class TenantAdminUsersController {

    private final TenantAdminUsersRepository repository;
    private final TenantAdminUsersService service;

    public TenantAdminUsersController(
            TenantAdminUsersRepository repository,
            TenantAdminUsersService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TenantAdminUsersResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TenantAdminUsersResponseDTO::new)
                .toList();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new TenantAdminUsersResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<?> saveTenantAdminUsers(@RequestBody TenantAdminUsersRequestDTO data) {
        TenantAdminUsers saved = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TenantAdminUsersResponseDTO(saved));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenantAdminUsers(@PathVariable Long id, @RequestBody TenantAdminUsersRequestDTO data) {
        try {
            TenantAdminUsers updated = service.update(id, data);
            return ResponseEntity.ok(new TenantAdminUsersResponseDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenantAdminUsers(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("TenantAdminUsers deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"));
    }
}
