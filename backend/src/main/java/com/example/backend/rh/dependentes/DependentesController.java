package com.example.backend.rh.dependentes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/dependentes")
public class DependentesController {

    private final DependentesRepository repository;
    private final DependentesService dependentesService;

    public DependentesController(
            DependentesRepository repository,
            DependentesService dependentesService
    ) {
        this.repository = repository;
        this.dependentesService = dependentesService;
    }

    @GetMapping
    public List<DependentesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DependentesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public DependentesResponseDTO getById(@PathVariable Integer id) {
        Dependentes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dependente nao encontrado"));

        return new DependentesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DependentesResponseDTO saveDependente(@RequestBody DependentesRequestDTO data) {
        Dependentes saved = dependentesService.criar(data);
        return new DependentesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public DependentesResponseDTO updateDependente(@PathVariable Integer id, @RequestBody DependentesRequestDTO data) {
        Dependentes updated = dependentesService.atualizar(id, data);
        return new DependentesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteDependente(@PathVariable Integer id) {
        dependentesService.excluir(id);
        return "Dependente deleted";
    }
}