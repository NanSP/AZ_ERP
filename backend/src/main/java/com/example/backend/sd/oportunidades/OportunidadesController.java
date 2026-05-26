package com.example.backend.sd.oportunidades;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/oportunidades")
public class OportunidadesController {

    private final OportunidadesRepository repository;
    private final OportunidadesService oportunidadesService;

    public OportunidadesController(
            OportunidadesRepository repository,
            OportunidadesService oportunidadesService
    ) {
        this.repository = repository;
        this.oportunidadesService = oportunidadesService;
    }

    @GetMapping
    public List<OportunidadesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OportunidadesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public OportunidadesResponseDTO getById(@PathVariable Integer id) {
        Oportunidades entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade nao encontrada"));

        return new OportunidadesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadesResponseDTO saveOportunidades(@RequestBody OportunidadesRequestDTO data) {
        Oportunidades saved = oportunidadesService.criar(data);
        return new OportunidadesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public OportunidadesResponseDTO updateOportunidades(@PathVariable Integer id, @RequestBody OportunidadesRequestDTO data) {
        Oportunidades updated = oportunidadesService.atualizar(id, data);
        return new OportunidadesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteOportunidades(@PathVariable Integer id) {
        oportunidadesService.excluir(id);
        return "Oportunidade deleted";
    }
}