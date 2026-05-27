package com.example.backend.mm.materiais;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/mm/materiais")
public class MateriaisController {

    private final MateriaisRepository repository;
    private final MateriaisService materiaisService;

    public MateriaisController(
            MateriaisRepository repository,
            MateriaisService materiaisService
    ) {
        this.repository = repository;
        this.materiaisService = materiaisService;
    }

    @GetMapping
    public List<MateriaisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MateriaisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public MateriaisResponseDTO getById(@PathVariable Integer id) {
        Materiais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Material nao encontrado"));

        return new MateriaisResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MateriaisResponseDTO saveMateriais(@RequestBody MateriaisRequestDTO data) {
        Materiais saved = materiaisService.criar(data);
        return new MateriaisResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public MateriaisResponseDTO updateMateriais(@PathVariable Integer id, @RequestBody MateriaisRequestDTO data) {
        Materiais updated = materiaisService.atualizar(id, data);
        return new MateriaisResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteMateriais(@PathVariable Integer id) {
        materiaisService.excluir(id);
        return "Material deleted";
    }
}