package com.example.backend.sys.permissoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/permissoes")
public class PermissoesController {

    private final PermissoesRepository repository;
    private final PermissoesService permissoesService;

    public PermissoesController(
            PermissoesRepository repository,
            PermissoesService permissoesService
    ) {
        this.repository = repository;
        this.permissoesService = permissoesService;
    }

    @GetMapping
    public List<PermissoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PermissoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PermissoesResponseDTO getById(@PathVariable Integer id) {
        Permissoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Permissao nao encontrada"));

        return new PermissoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissoesResponseDTO savePermissoes(@RequestBody PermissoesRequestDTO data) {
        Permissoes saved = permissoesService.criar(data);
        return new PermissoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PermissoesResponseDTO updatePermissoes(@PathVariable Integer id, @RequestBody PermissoesRequestDTO data) {
        Permissoes updated = permissoesService.atualizar(id, data);
        return new PermissoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePermissoes(@PathVariable Integer id) {
        permissoesService.excluir(id);
        return "Permissao deleted";
    }
}