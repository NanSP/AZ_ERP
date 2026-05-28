package com.example.backend.grc.auditorias;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/auditorias")
public class AuditoriasController {

    private final AuditoriasRepository repository;
    private final AuditoriasService auditoriasService;

    public AuditoriasController(
            AuditoriasRepository repository,
            AuditoriasService auditoriasService
    ) {
        this.repository = repository;
        this.auditoriasService = auditoriasService;
    }

    @GetMapping
    public List<AuditoriasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(AuditoriasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public AuditoriasResponseDTO getById(@PathVariable Integer id) {
        Auditorias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria nao encontrada"));

        return new AuditoriasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditoriasResponseDTO saveAuditorias(@RequestBody AuditoriasRequestDTO data) {
        Auditorias saved = auditoriasService.criar(data);
        return new AuditoriasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public AuditoriasResponseDTO updateAuditorias(@PathVariable Integer id, @RequestBody AuditoriasRequestDTO data) {
        Auditorias updated = auditoriasService.atualizar(id, data);
        return new AuditoriasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteAuditorias(@PathVariable Integer id) {
        auditoriasService.excluir(id);
        return "Auditoria deleted";
    }
}