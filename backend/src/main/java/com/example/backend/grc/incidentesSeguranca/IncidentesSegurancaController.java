package com.example.backend.grc.incidentesSeguranca;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/incidentesSeguranca")
public class IncidentesSegurancaController {

    private final IncidentesSegurancaRepository repository;
    private final IncidentesSegurancaService service;

    public IncidentesSegurancaController(
            IncidentesSegurancaRepository repository,
            IncidentesSegurancaService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<IncidentesSegurancaResponseDTO> getAll() {
        return repository.findAll().stream().map(IncidentesSegurancaResponseDTO::new).toList();
    }

    @GetMapping("/{id}")
    public IncidentesSegurancaResponseDTO getById(@PathVariable Integer id) {
        IncidentesSeguranca entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Incidente de seguranca nao encontrado"));
        return new IncidentesSegurancaResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentesSegurancaResponseDTO create(@RequestBody IncidentesSegurancaRequestDTO data) {
        return new IncidentesSegurancaResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public IncidentesSegurancaResponseDTO update(@PathVariable Integer id, @RequestBody IncidentesSegurancaRequestDTO data) {
        return new IncidentesSegurancaResponseDTO(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Incidente de seguranca deleted";
    }
}
