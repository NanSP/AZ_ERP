package com.example.backend.am.bensPatrimoniais;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/am/bensPatrimoniais")
public class BensPatrimoniaisController {

    private final BensPatrimoniaisRepository repository;
    private final BensPatrimoniaisService bensPatrimoniaisService;

    public BensPatrimoniaisController(
            BensPatrimoniaisRepository repository,
            BensPatrimoniaisService bensPatrimoniaisService
    ) {
        this.repository = repository;
        this.bensPatrimoniaisService = bensPatrimoniaisService;
    }

    @GetMapping
    public List<BensPatrimoniaisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BensPatrimoniaisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public BensPatrimoniaisResponseDTO getById(@PathVariable Integer id) {
        BensPatrimoniais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bem patrimonial nao encontrado"));

        return new BensPatrimoniaisResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BensPatrimoniaisResponseDTO saveBensPatrimoniais(@RequestBody BensPatrimoniaisRequestDTO data) {
        BensPatrimoniais saved = bensPatrimoniaisService.criar(data);
        return new BensPatrimoniaisResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public BensPatrimoniaisResponseDTO updateBensPatrimoniais(@PathVariable Integer id, @RequestBody BensPatrimoniaisRequestDTO data) {
        BensPatrimoniais updated = bensPatrimoniaisService.atualizar(id, data);
        return new BensPatrimoniaisResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteBensPatrimoniais(@PathVariable Integer id) {
        bensPatrimoniaisService.excluir(id);
        return "Bem patrimonial deleted";
    }
}