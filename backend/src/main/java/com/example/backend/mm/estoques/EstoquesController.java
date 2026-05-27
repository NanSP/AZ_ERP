package com.example.backend.mm.estoques;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/estoques")
public class EstoquesController {

    private final EstoquesRepository repository;
    private final EstoquesService estoquesService;

    public EstoquesController(
            EstoquesRepository repository,
            EstoquesService estoquesService
    ) {
        this.repository = repository;
        this.estoquesService = estoquesService;
    }

    @GetMapping
    public List<EstoquesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EstoquesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EstoquesResponseDTO getById(@PathVariable Integer id) {
        Estoques entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque nao encontrado"));

        return new EstoquesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstoquesResponseDTO saveEstoques(@RequestBody EstoquesRequestDTO data) {
        Estoques saved = estoquesService.criar(data);
        return new EstoquesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EstoquesResponseDTO updateEstoques(@PathVariable Integer id, @RequestBody EstoquesRequestDTO data) {
        Estoques updated = estoquesService.atualizar(id, data);
        return new EstoquesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEstoques(@PathVariable Integer id) {
        estoquesService.excluir(id);
        return "Estoque deleted";
    }
}