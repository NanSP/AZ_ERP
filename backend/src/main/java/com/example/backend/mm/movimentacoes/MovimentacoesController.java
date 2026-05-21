package com.example.backend.mm.movimentacoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/movimentacoes")
public class MovimentacoesController {

    private final MovimentacoesRepository repository;
    private final MovimentacoesService movimentacoesService;

    public MovimentacoesController(
            MovimentacoesRepository repository,
            MovimentacoesService movimentacoesService
    ) {
        this.repository = repository;
        this.movimentacoesService = movimentacoesService;
    }

    @GetMapping
    public List<MovimentacoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MovimentacoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public MovimentacoesResponseDTO getById(@PathVariable Integer id) {
        Movimentacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao nao encontrada"));

        return new MovimentacoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacoesResponseDTO saveMovimentacoes(@RequestBody MovimentacoesRequestDTO data) {
        Movimentacoes saved = movimentacoesService.criar(data);
        return new MovimentacoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public MovimentacoesResponseDTO updateMovimentacoes(@PathVariable Integer id, @RequestBody MovimentacoesRequestDTO data) {
        Movimentacoes updated = movimentacoesService.atualizar(id, data);
        return new MovimentacoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteMovimentacoes(@PathVariable Integer id) {
        movimentacoesService.excluir(id);
        return "Movimentacao deleted";
    }
}