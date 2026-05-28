package com.example.backend.fiscal.esocialEventos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/esocialEventos")
public class EsocialEventosController {

    private final EsocialEventosRepository repository;
    private final EsocialEventosService esocialEventosService;

    public EsocialEventosController(
            EsocialEventosRepository repository,
            EsocialEventosService esocialEventosService
    ) {
        this.repository = repository;
        this.esocialEventosService = esocialEventosService;
    }

    @GetMapping
    public List<EsocialEventosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EsocialEventosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EsocialEventosResponseDTO getById(@PathVariable Long id) {
        EsocialEventos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento eSocial nao encontrado"));

        return new EsocialEventosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EsocialEventosResponseDTO saveEsocialEventos(@RequestBody EsocialEventosRequestDTO data) {
        EsocialEventos saved = esocialEventosService.criar(data);
        return new EsocialEventosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EsocialEventosResponseDTO updateEsocialEventos(@PathVariable Long id, @RequestBody EsocialEventosRequestDTO data) {
        EsocialEventos updated = esocialEventosService.atualizar(id, data);
        return new EsocialEventosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEsocialEventos(@PathVariable Long id) {
        esocialEventosService.excluir(id);
        return "eSocial evento deleted";
    }
}