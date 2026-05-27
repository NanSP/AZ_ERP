package com.example.backend.rh.colaboradores;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/colaboradores")
public class ColaboradoresController {

    private final ColaboradoresRepository repository;
    private final ColaboradoresService colaboradoresService;

    public ColaboradoresController(
            ColaboradoresRepository repository,
            ColaboradoresService colaboradoresService
    ) {
        this.repository = repository;
        this.colaboradoresService = colaboradoresService;
    }

    @GetMapping
    public List<ColaboradoresResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ColaboradoresResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ColaboradoresResponseDTO getById(@PathVariable Integer id) {
        Colaboradores entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));

        return new ColaboradoresResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradoresResponseDTO saveColaborador(@RequestBody ColaboradoresRequestDTO data) {
        Colaboradores saved = colaboradoresService.criar(data);
        return new ColaboradoresResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ColaboradoresResponseDTO updateColaborador(@PathVariable Integer id, @RequestBody ColaboradoresRequestDTO data) {
        Colaboradores updated = colaboradoresService.atualizar(id, data);
        return new ColaboradoresResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteColaborador(@PathVariable Integer id) {
        colaboradoresService.excluir(id);
        return "Colaborador deleted";
    }
}