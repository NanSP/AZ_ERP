package com.example.backend.master.platform.tenantDatabases;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/tenantDatabases")
public class TenantDatabasesController {

    private final TenantDatabasesRepository repository;
    private final TenantDatabasesService tenantDatabasesService;

    public TenantDatabasesController(
            TenantDatabasesRepository repository,
            TenantDatabasesService service
    ) {
        this.repository = repository;
        this.tenantDatabasesService = service;
    }

    @GetMapping
    public List<TenantDatabasesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TenantDatabasesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TenantDatabasesResponseDTO getById(@PathVariable Long id) {
        TenantDatabases entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant database nao encontrado"));

        return new TenantDatabasesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantDatabasesResponseDTO saveTenantDatabases(@RequestBody TenantDatabasesRequestDTO data) {
        TenantDatabases saved = tenantDatabasesService.criar(data);
        return new TenantDatabasesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public TenantDatabasesResponseDTO updateTenantDatabases(@PathVariable Long id, @RequestBody TenantDatabasesRequestDTO data) {
        TenantDatabases updated = tenantDatabasesService.atualizar(id, data);
        return new TenantDatabasesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteTenantDatabases(@PathVariable Long id) {
        tenantDatabasesService.excluir(id);
        return "Tenant database deleted";
    }
}
