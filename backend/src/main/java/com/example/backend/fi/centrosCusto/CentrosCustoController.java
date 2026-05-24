package com.example.backend.fi.centrosCusto;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/centrosCusto")
public class CentrosCustoController {

    private final CentrosCustoRepository repository;
    private final CentrosCustoService centrosCustoService;

    public CentrosCustoController(
            CentrosCustoRepository repository,
            CentrosCustoService centrosCustoService
    ) {
        this.repository = repository;
        this.centrosCustoService = centrosCustoService;
    }

    @GetMapping
    public List<CentrosCustoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(CentrosCustoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public CentrosCustoResponseDTO getById(@PathVariable Integer id) {
        CentrosCusto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo nao encontrado"));

        return new CentrosCustoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentrosCustoResponseDTO saveCentrosCusto(@RequestBody CentrosCustoRequestDTO data) {
        CentrosCusto saved = centrosCustoService.criar(data);
        return new CentrosCustoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public CentrosCustoResponseDTO updateCentrosCusto(@PathVariable Integer id, @RequestBody CentrosCustoRequestDTO data) {
        CentrosCusto updated = centrosCustoService.atualizar(id, data);
        return new CentrosCustoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteCentrosCusto(@PathVariable Integer id) {
        centrosCustoService.excluir(id);
        return "Centro de custo deleted";
    }
}