package com.example.backend.pp.bom;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/bom")
public class BomController {

    private final BomRepository repository;
    private final BomService bomService;

    public BomController(
            BomRepository repository,
            BomService bomService
    ) {
        this.repository = repository;
        this.bomService = bomService;
    }

    @GetMapping
    public List<BomResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BomResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public BomResponseDTO getById(@PathVariable Integer id) {
        Bom entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("BOM nao encontrado"));

        return new BomResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BomResponseDTO saveBom(@RequestBody BomRequestDTO data) {
        Bom saved = bomService.criar(data);
        return new BomResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public BomResponseDTO updateBom(@PathVariable Integer id, @RequestBody BomRequestDTO data) {
        Bom updated = bomService.atualizar(id, data);
        return new BomResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteBom(@PathVariable Integer id) {
        bomService.excluir(id);
        return "BOM deleted";
    }
}