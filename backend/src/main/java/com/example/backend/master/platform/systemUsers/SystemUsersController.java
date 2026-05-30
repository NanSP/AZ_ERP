package com.example.backend.master.platform.systemUsers;


import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/systemUsers")
public class SystemUsersController {

    private SystemUsersRepository repository;
    private final SystemUsersService systemUsersService;

    public SystemUsersController(
            SystemUsersRepository repository,
            SystemUsersService systemUsersService
    ) {
        this.repository = repository;
        this.systemUsersService = systemUsersService;
    }

    @GetMapping
    public List<SystemUsersResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SystemUsersResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public SystemUsersResponseDTO getById(@PathVariable Long id) {
        SystemUsers entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("System user nao encontrado"));

        return new SystemUsersResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SystemUsersResponseDTO saveSystemUsers(@RequestBody SystemUsersRequestDTO data) {
        SystemUsers saved = systemUsersService.criar(data);
        return new SystemUsersResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public SystemUsersResponseDTO updateSystemUsers(@PathVariable Long id, @RequestBody SystemUsersRequestDTO data) {
        SystemUsers updated = systemUsersService.atualizar(id, data);
        return new SystemUsersResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteSystemUsers(@PathVariable Long id) {
        systemUsersService.excluir(id);
        return "System user deleted";
    }
}
