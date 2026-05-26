package com.example.backend.ps.recursosAlocados;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/recursosAlocados")
public class RecursosAlocadosController {

    private final RecursosAlocadosRepository repository;
    private final RecursosAlocadosService recursosAlocadosService;

    public RecursosAlocadosController(
            RecursosAlocadosRepository repository,
            RecursosAlocadosService recursosAlocadosService
    ) {
        this.repository = repository;
        this.recursosAlocadosService = recursosAlocadosService;
    }

    @GetMapping
    public List<RecursosAlocadosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RecursosAlocadosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public RecursosAlocadosResponseDTO getById(@PathVariable Integer id) {
        RecursosAlocados entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso alocado nao encontrado"));

        return new RecursosAlocadosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursosAlocadosResponseDTO saveRecursosAlocados(@RequestBody RecursosAlocadosRequestDTO data) {
        RecursosAlocados saved = recursosAlocadosService.criar(data);
        return new RecursosAlocadosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public RecursosAlocadosResponseDTO updateRecursosAlocados(@PathVariable Integer id, @RequestBody RecursosAlocadosRequestDTO data) {
        RecursosAlocados updated = recursosAlocadosService.atualizar(id, data);
        return new RecursosAlocadosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteRecursosAlocados(@PathVariable Integer id) {
        recursosAlocadosService.excluir(id);
        return "Recurso alocado deleted";
    }
}