package com.example.backend.fi.fluxoCaixa;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/fluxoCaixa")
public class FluxoCaixaController {

    private final FluxoCaixaRepository repository;
    private final FluxoCaixaService fluxoCaixaService;

    public FluxoCaixaController(
            FluxoCaixaRepository repository,
            FluxoCaixaService fluxoCaixaService
    ) {
        this.repository = repository;
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @GetMapping
    public List<FluxoCaixaResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FluxoCaixaResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public FluxoCaixaResponseDTO getById(@PathVariable Integer id) {
        FluxoCaixa entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fluxo de caixa nao encontrado"));

        return new FluxoCaixaResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FluxoCaixaResponseDTO saveFluxoCaixa(@RequestBody FluxoCaixaRequestDTO data) {
        FluxoCaixa saved = fluxoCaixaService.criar(data);
        return new FluxoCaixaResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public FluxoCaixaResponseDTO updateFluxoCaixa(@PathVariable Integer id, @RequestBody FluxoCaixaRequestDTO data) {
        FluxoCaixa updated = fluxoCaixaService.atualizar(id, data);
        return new FluxoCaixaResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteFluxoCaixa(@PathVariable Integer id) {
        fluxoCaixaService.excluir(id);
        return "Fluxo de Caixa deleted";
    }
}