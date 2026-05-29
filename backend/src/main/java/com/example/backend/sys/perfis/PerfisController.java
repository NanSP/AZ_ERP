package com.example.backend.sys.perfis;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/perfis")
public class PerfisController {

    private final PerfisRepository repository;
    private final PerfisService perfisService;

    public PerfisController(
            PerfisRepository repository,
            PerfisService perfisService
    ) {
        this.repository = repository;
        this.perfisService = perfisService;
    }

    @GetMapping
    public List<PerfisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PerfisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PerfisResponseDTO getById(@PathVariable Integer id) {
        Perfis entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));

        return new PerfisResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PerfisResponseDTO savePerfis(@RequestBody PerfisRequestDTO data) {
        Perfis saved = perfisService.criar(data);
        return new PerfisResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PerfisResponseDTO updatePerfis(@PathVariable Integer id, @RequestBody PerfisRequestDTO data) {
        Perfis updated = perfisService.atualizar(id, data);
        return new PerfisResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePerfis(@PathVariable Integer id) {
        perfisService.excluir(id);
        return "Perfil deleted";
    }
}