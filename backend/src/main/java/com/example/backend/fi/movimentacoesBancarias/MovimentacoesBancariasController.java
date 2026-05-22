package com.example.backend.fi.movimentacoesBancarias;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/movimentacoesBancarias")
public class MovimentacoesBancariasController {

    private final MovimentacoesBancariasRepository repository;
    private final MovimentacoesBancariasService movimentacoesBancariasService;

    public MovimentacoesBancariasController(
            MovimentacoesBancariasRepository repository,
            MovimentacoesBancariasService movimentacoesBancariasService
    ) {
        this.repository = repository;
        this.movimentacoesBancariasService = movimentacoesBancariasService;
    }

    @GetMapping
    public List<MovimentacoesBancariasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MovimentacoesBancariasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public MovimentacoesBancariasResponseDTO getById(@PathVariable Integer id) {
        MovimentacoesBancarias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao bancaria nao encontrada"));

        return new MovimentacoesBancariasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacoesBancariasResponseDTO saveMovimentacoesBancarias(@RequestBody MovimentacoesBancariasRequestDTO data) {
        MovimentacoesBancarias saved = movimentacoesBancariasService.criar(data);
        return new MovimentacoesBancariasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public MovimentacoesBancariasResponseDTO updateMovimentacoesBancarias(@PathVariable Integer id, @RequestBody MovimentacoesBancariasRequestDTO data) {
        MovimentacoesBancarias updated = movimentacoesBancariasService.atualizar(id, data);
        return new MovimentacoesBancariasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteMovimentacoesBancarias(@PathVariable Integer id) {
        movimentacoesBancariasService.excluir(id);
        return "Movimentacao bancaria deleted";
    }
}