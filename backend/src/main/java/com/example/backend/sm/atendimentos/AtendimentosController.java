package com.example.backend.sm.atendimentos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/atendimentos")
public class AtendimentosController {

    private final AtendimentosRepository repository;
    private final AtendimentosService atendimentosService;

    public AtendimentosController(
            AtendimentosRepository repository,
            AtendimentosService atendimentosService
    ) {
        this.repository = repository;
        this.atendimentosService = atendimentosService;
    }

    @GetMapping
    public List<AtendimentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(AtendimentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public AtendimentosResponseDTO getById(@PathVariable Integer id) {
        Atendimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento nao encontrado"));

        return new AtendimentosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentosResponseDTO saveAtendimentos(@RequestBody AtendimentosRequestDTO data) {
        Atendimentos saved = atendimentosService.criar(data);
        return new AtendimentosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public AtendimentosResponseDTO updateAtendimentos(@PathVariable Integer id, @RequestBody AtendimentosRequestDTO data) {
        Atendimentos updated = atendimentosService.atualizar(id, data);
        return new AtendimentosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteAtendimentos(@PathVariable Integer id) {
        atendimentosService.excluir(id);
        return "Atendimento deleted";
    }
}