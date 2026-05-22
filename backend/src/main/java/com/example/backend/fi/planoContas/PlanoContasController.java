package com.example.backend.fi.planoContas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/planoContas")
public class PlanoContasController {

    private final PlanoContasRepository repository;
    private final PlanoContasService planoContasService;

    public PlanoContasController(
            PlanoContasRepository repository,
            PlanoContasService planoContasService
    ) {
        this.repository = repository;
        this.planoContasService = planoContasService;
    }

    @GetMapping
    public List<PlanoContasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PlanoContasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PlanoContasResponseDTO getById(@PathVariable Integer id) {
        PlanoContas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de contas nao encontrado"));

        return new PlanoContasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoContasResponseDTO savePlanoContas(@RequestBody PlanoContasRequestDTO data) {
        PlanoContas saved = planoContasService.criar(data);
        return new PlanoContasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PlanoContasResponseDTO updatePlanoContas(@PathVariable Integer id, @RequestBody PlanoContasRequestDTO data) {
        PlanoContas updated = planoContasService.atualizar(id, data);
        return new PlanoContasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePlanoContas(@PathVariable Integer id) {
        planoContasService.excluir(id);
        return "Plano Contas deleted";
    }
}
