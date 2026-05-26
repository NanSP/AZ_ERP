package com.example.backend.sm.slaConfig;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/slaConfig")
public class SlaConfigController {

    private final SlaConfigRepository repository;
    private final SlaConfigService slaConfigService;

    public SlaConfigController(
            SlaConfigRepository repository,
            SlaConfigService slaConfigService
    ) {
        this.repository = repository;
        this.slaConfigService = slaConfigService;
    }

    @GetMapping
    public List<SlaConfigResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SlaConfigResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public SlaConfigResponseDTO getById(@PathVariable Integer id) {
        SlaConfig entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configuracao de SLA nao encontrada"));

        return new SlaConfigResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlaConfigResponseDTO saveSlaConfig(@RequestBody SlaConfigRequestDTO data) {
        SlaConfig saved = slaConfigService.criar(data);
        return new SlaConfigResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public SlaConfigResponseDTO updateSlaConfig(@PathVariable Integer id, @RequestBody SlaConfigRequestDTO data) {
        SlaConfig updated = slaConfigService.atualizar(id, data);
        return new SlaConfigResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteSlaConfig(@PathVariable Integer id) {
        slaConfigService.excluir(id);
        return "SLA config deleted";
    }
}