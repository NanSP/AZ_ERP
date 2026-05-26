package com.example.backend.sd.faturas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/faturas")
public class FaturasController {

    private final FaturasRepository repository;
    private final FaturasService faturasService;

    public FaturasController(
            FaturasRepository repository,
            FaturasService faturasService
    ) {
        this.repository = repository;
        this.faturasService = faturasService;
    }

    @GetMapping
    public List<FaturasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FaturasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public FaturasResponseDTO getById(@PathVariable Integer id) {
        Faturas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fatura nao encontrada"));

        return new FaturasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaturasResponseDTO saveFaturas(@RequestBody FaturasRequestDTO data) {
        Faturas saved = faturasService.criar(data);
        return new FaturasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public FaturasResponseDTO updateFaturas(@PathVariable Integer id, @RequestBody FaturasRequestDTO data) {
        Faturas updated = faturasService.atualizar(id, data);
        return new FaturasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteFaturas(@PathVariable Integer id) {
        faturasService.excluir(id);
        return "Fatura deleted";
    }
}