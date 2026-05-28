package com.example.backend.grc.controles;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/controles")
public class ControlesController {

    private final ControlesRepository repository;
    private final ControlesService controlesService;

    public ControlesController(
            ControlesRepository repository,
            ControlesService controlesService
    ) {
        this.repository = repository;
        this.controlesService = controlesService;
    }

    @GetMapping
    public List<ControlesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ControlesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ControlesResponseDTO getById(@PathVariable Integer id) {
        Controles entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle nao encontrado"));

        return new ControlesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ControlesResponseDTO saveControles(@RequestBody ControlesRequestDTO data) {
        Controles saved = controlesService.criar(data);
        return new ControlesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ControlesResponseDTO updateControles(@PathVariable Integer id, @RequestBody ControlesRequestDTO data) {
        Controles updated = controlesService.atualizar(id, data);
        return new ControlesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteControles(@PathVariable Integer id) {
        controlesService.excluir(id);
        return "Controle deleted";
    }
}