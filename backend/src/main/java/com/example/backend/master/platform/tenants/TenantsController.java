package com.example.backend.master.platform.tenants;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/tenants")
public class TenantsController {

    private final TenantsRepository repository;
    private final TenantsService tenantsService;

    public TenantsController(
            TenantsRepository repository,
            TenantsService tenantsService
    ) {
        this.repository = repository;
        this.tenantsService = tenantsService;
    }

    @GetMapping
    public List<TenantsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TenantsResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TenantsResponseDTO getById(@PathVariable Long id) {
        Tenants entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant nao encontrado"));

        return new TenantsResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantsResponseDTO saveTenants(@RequestBody TenantsRequestDTO data) {
        Tenants saved = tenantsService.criar(data);
        return new TenantsResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public TenantsResponseDTO updateTenants(@PathVariable Long id, @RequestBody TenantsRequestDTO data) {
        Tenants updated = tenantsService.atualizar(id, data);
        return new TenantsResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteTenants(@PathVariable Long id) {
        tenantsService.excluir(id);
        return "Tenant deleted";
    }
}
