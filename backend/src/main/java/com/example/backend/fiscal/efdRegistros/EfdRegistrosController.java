package com.example.backend.fiscal.efdRegistros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/efdRegistros")
public class EfdRegistrosController {

    private final EfdRegistrosRepository repository;
    private final EfdRegistrosService efdRegistrosService;

    public EfdRegistrosController(
            EfdRegistrosRepository repository,
            EfdRegistrosService efdRegistrosService
    ) {
        this.repository = repository;
        this.efdRegistrosService = efdRegistrosService;
    }

    @GetMapping
    public List<EfdRegistrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EfdRegistrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EfdRegistrosResponseDTO getById(@PathVariable Long id) {
        EfdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro EFD nao encontrado"));

        return new EfdRegistrosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EfdRegistrosResponseDTO saveEfdRegistros(@RequestBody EfdRegistrosRequestDTO data) {
        EfdRegistros saved = efdRegistrosService.criar(data);
        return new EfdRegistrosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EfdRegistrosResponseDTO updateEfdRegistros(@PathVariable Long id, @RequestBody EfdRegistrosRequestDTO data) {
        EfdRegistros updated = efdRegistrosService.atualizar(id, data);
        return new EfdRegistrosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEfdRegistros(@PathVariable Long id) {
        efdRegistrosService.excluir(id);
        return "EFD registro deleted";
    }
}