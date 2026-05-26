package com.example.backend.pp.apontamentos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/apontamentos")
public class ApontamentosController {

    private final ApontamentosRepository repository;
    private final ApontamentosService apontamentosService;

    public ApontamentosController(
            ApontamentosRepository repository,
            ApontamentosService apontamentosService
    ) {
        this.repository = repository;
        this.apontamentosService = apontamentosService;
    }

    @GetMapping
    public List<ApontamentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ApontamentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ApontamentosResponseDTO getById(@PathVariable Integer id) {
        Apontamentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Apontamento nao encontrado"));

        return new ApontamentosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApontamentosResponseDTO saveApontamentos(@RequestBody ApontamentosRequestDTO data) {
        Apontamentos saved = apontamentosService.criar(data);
        return new ApontamentosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ApontamentosResponseDTO updateApontamentos(@PathVariable Integer id, @RequestBody ApontamentosRequestDTO data) {
        Apontamentos updated = apontamentosService.atualizar(id, data);
        return new ApontamentosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteApontamentos(@PathVariable Integer id) {
        apontamentosService.excluir(id);
        return "Apontamento deleted";
    }
}