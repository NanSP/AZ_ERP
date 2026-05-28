package com.example.backend.portal.sessoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/sessoes")
public class SessoesController {

    private final SessoesRepository repository;
    private final SessoesService sessoesService;

    public SessoesController(
            SessoesRepository repository,
            SessoesService sessoesService
    ) {
        this.repository = repository;
        this.sessoesService = sessoesService;
    }

    @GetMapping
    public List<SessoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SessoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public SessoesResponseDTO getById(@PathVariable Integer id) {
        Sessoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));

        return new SessoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessoesResponseDTO saveSessoes(@RequestBody SessoesRequestDTO data) {
        Sessoes saved = sessoesService.criar(data);
        return new SessoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public SessoesResponseDTO updateSessoes(@PathVariable Integer id, @RequestBody SessoesRequestDTO data) {
        Sessoes updated = sessoesService.atualizar(id, data);
        return new SessoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteSessoes(@PathVariable Integer id) {
        sessoesService.excluir(id);
        return "Sessao deleted";
    }
}