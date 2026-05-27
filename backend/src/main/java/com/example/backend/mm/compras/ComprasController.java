package com.example.backend.mm.compras;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/compras")
public class ComprasController {

    private final ComprasRepository repository;
    private final ComprasService comprasService;

    public ComprasController(
            ComprasRepository repository,
            ComprasService comprasService
    ) {
        this.repository = repository;
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<ComprasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ComprasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ComprasResponseDTO getById(@PathVariable Integer id) {
        Compras entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra nao encontrada"));

        return new ComprasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComprasResponseDTO saveCompras(@RequestBody ComprasRequestDTO data) {
        Compras saved = comprasService.criar(data);
        return new ComprasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ComprasResponseDTO updateCompras(@PathVariable Integer id, @RequestBody ComprasRequestDTO data) {
        Compras updated = comprasService.atualizar(id, data);
        return new ComprasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteCompras(@PathVariable Integer id) {
        comprasService.excluir(id);
        return "Compra deleted";
    }
}