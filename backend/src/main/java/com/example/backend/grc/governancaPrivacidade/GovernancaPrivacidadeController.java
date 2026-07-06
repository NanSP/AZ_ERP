package com.example.backend.grc.governancaPrivacidade;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/governancaPrivacidade")
public class GovernancaPrivacidadeController {

    private final GovernancaPrivacidadeRepository repository;
    private final GovernancaPrivacidadeService service;

    public GovernancaPrivacidadeController(
            GovernancaPrivacidadeRepository repository,
            GovernancaPrivacidadeService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<GovernancaPrivacidadeResponseDTO> getAll() {
        return repository.findAll().stream().map(GovernancaPrivacidadeResponseDTO::new).toList();
    }

    @GetMapping("/{id}")
    public GovernancaPrivacidadeResponseDTO getById(@PathVariable Integer id) {
        GovernancaPrivacidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Governanca de privacidade nao encontrada"));
        return new GovernancaPrivacidadeResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GovernancaPrivacidadeResponseDTO create(@RequestBody GovernancaPrivacidadeRequestDTO data) {
        return new GovernancaPrivacidadeResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public GovernancaPrivacidadeResponseDTO update(@PathVariable Integer id, @RequestBody GovernancaPrivacidadeRequestDTO data) {
        return new GovernancaPrivacidadeResponseDTO(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Governanca de privacidade deleted";
    }
}
