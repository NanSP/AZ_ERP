package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/solicitacoesTitular")
public class SolicitacoesTitularController {

    private final SolicitacoesTitularRepository repository;
    private final SolicitacoesTitularService service;

    public SolicitacoesTitularController(
            SolicitacoesTitularRepository repository,
            SolicitacoesTitularService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<SolicitacoesTitularResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SolicitacoesTitularResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public SolicitacoesTitularResponseDTO getById(@PathVariable Integer id) {
        SolicitacoesTitular entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao do titular nao encontrada"));
        return new SolicitacoesTitularResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacoesTitularResponseDTO create(@RequestBody SolicitacoesTitularRequestDTO data) {
        return new SolicitacoesTitularResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public SolicitacoesTitularResponseDTO update(@PathVariable Integer id, @RequestBody SolicitacoesTitularRequestDTO data) {
        return new SolicitacoesTitularResponseDTO(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Solicitacao do titular deleted";
    }
}
