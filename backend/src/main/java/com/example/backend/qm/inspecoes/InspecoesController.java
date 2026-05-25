package com.example.backend.qm.inspecoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/qm/inspecoes")
public class InspecoesController {

    private final InspecoesRepository repository;
    private final InspecoesService inspecoesService;

    public InspecoesController(
            InspecoesRepository repository,
            InspecoesService inspecoesService
    ) {
        this.repository = repository;
        this.inspecoesService = inspecoesService;
    }

    @GetMapping
    public List<InspecoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(InspecoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public InspecoesResponseDTO getById(@PathVariable Integer id) {
        Inspecoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inspecao nao encontrada"));

        return new InspecoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspecoesResponseDTO saveInspecoes(@RequestBody InspecoesRequestDTO data) {
        Inspecoes saved = inspecoesService.criar(data);
        return new InspecoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public InspecoesResponseDTO updateInspecoes(@PathVariable Integer id, @RequestBody InspecoesRequestDTO data) {
        Inspecoes updated = inspecoesService.atualizar(id, data);
        return new InspecoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteInspecoes(@PathVariable Integer id) {
        inspecoesService.excluir(id);
        return "Inspecao deleted";
    }
}