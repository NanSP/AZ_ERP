package com.example.backend.grc.consentimentos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/consentimentos")
public class ConsentimentosController {

    private final ConsentimentosRepository repository;
    private final ConsentimentosService consentimentosService;

    public ConsentimentosController(
            ConsentimentosRepository repository,
            ConsentimentosService consentimentosService
    ) {
        this.repository = repository;
        this.consentimentosService = consentimentosService;
    }

    @GetMapping
    public List<ConsentimentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ConsentimentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ConsentimentosResponseDTO getById(@PathVariable Integer id) {
        Consentimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consentimento nao encontrado"));

        return new ConsentimentosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentimentosResponseDTO saveConsentimentos(@RequestBody ConsentimentosRequestDTO data) {
        Consentimentos saved = consentimentosService.criar(data);
        return new ConsentimentosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ConsentimentosResponseDTO updateConsentimentos(@PathVariable Integer id, @RequestBody ConsentimentosRequestDTO data) {
        Consentimentos updated = consentimentosService.atualizar(id, data);
        return new ConsentimentosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteConsentimentos(@PathVariable Integer id) {
        consentimentosService.excluir(id);
        return "Consentimento deleted";
    }
}