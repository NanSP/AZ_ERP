package com.example.backend.auditoria.logAcoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria/logAcoes")
public class LogAcoesController {

    private final LogAcoesRepository repository;
    private final LogAcoesService logAcoesService;

    public LogAcoesController(
            LogAcoesRepository repository,
            LogAcoesService logAcoesService
    ) {
        this.repository = repository;
        this.logAcoesService = logAcoesService;
    }

    @GetMapping
    public List<LogAcoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(LogAcoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public LogAcoesResponseDTO getById(@PathVariable Long id) {
        LogAcoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Log de acao nao encontrado"));

        return new LogAcoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogAcoesResponseDTO saveLogAcoes(@RequestBody LogAcoesRequestDTO data) {
        LogAcoes saved = logAcoesService.criar(data);
        return new LogAcoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public LogAcoesResponseDTO updateLogAcoes(@PathVariable Long id, @RequestBody LogAcoesRequestDTO data) {
        LogAcoes updated = logAcoesService.atualizar(id, data);
        return new LogAcoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteLogAcoes(@PathVariable Long id) {
        logAcoesService.excluir(id);
        return "Log de acao deleted";
    }
}