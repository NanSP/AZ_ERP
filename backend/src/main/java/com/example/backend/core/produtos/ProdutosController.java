package com.example.backend.core.produtos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/produtos")
public class ProdutosController {

    private final ProdutosRepository repository;
    private final ProdutosService produtosService;

    public ProdutosController(
            ProdutosRepository repository,
            ProdutosService produtosService
    ) {
        this.repository = repository;
        this.produtosService = produtosService;
    }

    @GetMapping
    public List<ProdutosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProdutosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ProdutosResponseDTO getById(@PathVariable Integer id) {
        Produtos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        return new ProdutosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutosResponseDTO saveProduto(@RequestBody ProdutosRequestDTO data) {
        Produtos saved = produtosService.criar(data);
        return new ProdutosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ProdutosResponseDTO updateProduto(@PathVariable Integer id, @RequestBody ProdutosRequestDTO data) {
        Produtos updated = produtosService.atualizar(id, data);
        return new ProdutosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteProduto(@PathVariable Integer id) {
        produtosService.excluir(id);
        return "Produto deleted";
    }
}