package com.example.backend.master.platform.tenantAdminUsers;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/tenantAdminUsers")
public class TenantAdminUsersController {

    private final TenantAdminUsersRepository repository;
    private final TenantAdminUsersService tenantAdminUsersService;

    public TenantAdminUsersController(
            TenantAdminUsersRepository repository,
            TenantAdminUsersService service
    ) {
        this.repository = repository;
        this.tenantAdminUsersService = service;
    }

    @GetMapping
    public List<TenantAdminUsersResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TenantAdminUsersResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TenantAdminUsersResponseDTO getById(@PathVariable Long id) {
        TenantAdminUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tenant admin user nao encontrado"));

        return new TenantAdminUsersResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantAdminUsersResponseDTO saveTenantAdminUsers(@RequestBody TenantAdminUsersRequestDTO data) {
        TenantAdminUsers saved = tenantAdminUsersService.criar(data);
        return new TenantAdminUsersResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public TenantAdminUsersResponseDTO updateTenantAdminUsers(@PathVariable Long id, @RequestBody TenantAdminUsersRequestDTO data) {
        TenantAdminUsers updated = tenantAdminUsersService.atualizar(id, data);
        return new TenantAdminUsersResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteTenantAdminUsers(@PathVariable Long id) {
        tenantAdminUsersService.excluir(id);
        return "Tenant admin user deleted";
    }
}
