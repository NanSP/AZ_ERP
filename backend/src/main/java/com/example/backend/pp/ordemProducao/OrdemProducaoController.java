package com.example.backend.pp.ordemProducao;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/ordemProducao")
public class OrdemProducaoController {

    private final OrdemProducaoRepository repository;
    private final OrdemProducaoService ordemProducaoService;

    public OrdemProducaoController(
            OrdemProducaoRepository repository,
            OrdemProducaoService ordemProducaoService
    ) {
        this.repository = repository;
        this.ordemProducaoService = ordemProducaoService;
    }

    @GetMapping
    public List<OrdemProducaoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OrdemProducaoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public OrdemProducaoResponseDTO getById(@PathVariable Integer id) {
        OrdemProducao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de producao nao encontrada"));

        return new OrdemProducaoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdemProducaoResponseDTO saveOrdemProducao(@RequestBody OrdemProducaoRequestDTO data) {
        OrdemProducao saved = ordemProducaoService.criar(data);
        return new OrdemProducaoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public OrdemProducaoResponseDTO updateOrdemProducao(@PathVariable Integer id, @RequestBody OrdemProducaoRequestDTO data) {
        OrdemProducao updated = ordemProducaoService.atualizar(id, data);
        return new OrdemProducaoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteOrdemProducao(@PathVariable Integer id) {
        ordemProducaoService.excluir(id);
        return "Ordem Producao deleted";
    }
}