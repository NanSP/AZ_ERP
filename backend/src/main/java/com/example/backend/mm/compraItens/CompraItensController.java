package com.example.backend.mm.compraItens;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/compraItens")
public class CompraItensController {

    private final CompraItensRepository repository;
    private final CompraItensService compraItensService;

    public CompraItensController(
            CompraItensRepository repository,
            CompraItensService compraItensService
    ) {
        this.repository = repository;
        this.compraItensService = compraItensService;
    }

    @GetMapping
    public List<CompraItensResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(CompraItensResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public CompraItensResponseDTO getById(@PathVariable Integer id) {
        CompraItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da compra nao encontrado"));

        return new CompraItensResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraItensResponseDTO saveCompraItens(@RequestBody CompraItensRequestDTO data) {
        CompraItens saved = compraItensService.criar(data);
        return new CompraItensResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public CompraItensResponseDTO updateCompraItens(@PathVariable Integer id, @RequestBody CompraItensRequestDTO data) {
        CompraItens updated = compraItensService.atualizar(id, data);
        return new CompraItensResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteCompraItens(@PathVariable Integer id) {
        compraItensService.excluir(id);
        return "Compra Item deleted";
    }
}
