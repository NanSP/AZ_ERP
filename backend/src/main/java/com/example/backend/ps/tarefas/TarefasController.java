package com.example.backend.ps.tarefas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/tarefas")
public class TarefasController {

    private final TarefasRepository repository;
    private final TarefasService tarefasService;

    public TarefasController(
            TarefasRepository repository,
            TarefasService tarefasService
    ) {
        this.repository = repository;
        this.tarefasService = tarefasService;
    }

    @GetMapping
    public List<TarefasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TarefasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TarefasResponseDTO getById(@PathVariable Integer id) {
        Tarefas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada"));

        return new TarefasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarefasResponseDTO saveTarefas(@RequestBody TarefasRequestDTO data) {
        Tarefas saved = tarefasService.criar(data);
        return new TarefasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public TarefasResponseDTO updateTarefas(@PathVariable Integer id, @RequestBody TarefasRequestDTO data) {
        Tarefas updated = tarefasService.atualizar(id, data);
        return new TarefasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteTarefas(@PathVariable Integer id) {
        tarefasService.excluir(id);
        return "Tarefa deleted";
    }
}