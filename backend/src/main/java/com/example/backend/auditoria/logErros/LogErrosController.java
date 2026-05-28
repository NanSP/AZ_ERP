package com.example.backend.auditoria.logErros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria/logErros")
public class LogErrosController {

    private final LogErrosRepository repository;
    private final LogErrosService logErrosService;

    public LogErrosController(
            LogErrosRepository repository,
            LogErrosService logErrosService
    ) {
        this.repository = repository;
        this.logErrosService = logErrosService;
    }

    @GetMapping
    public List<LogErrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(LogErrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public LogErrosResponseDTO getById(@PathVariable Long id) {
        LogErros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Log de erro nao encontrado"));

        return new LogErrosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogErrosResponseDTO saveLogErros(@RequestBody LogErrosRequestDTO data) {
        LogErros saved = logErrosService.criar(data);
        return new LogErrosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public LogErrosResponseDTO updateLogErros(@PathVariable Long id, @RequestBody LogErrosRequestDTO data) {
        LogErros updated = logErrosService.atualizar(id, data);
        return new LogErrosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteLogErros(@PathVariable Long id) {
        logErrosService.excluir(id);
        return "Log de erro deleted";
    }
}